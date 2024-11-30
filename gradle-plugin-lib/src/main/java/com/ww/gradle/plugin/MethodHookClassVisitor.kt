package com.ww.gradle.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import java.io.File

class MethodHookClassVisitor(
    api: Int,
    cv: ClassVisitor,
    private val config: MethodHookConfig
) : ClassVisitor(api, cv) {

    private var isInterface: Boolean = false
    private lateinit var className: String

    private fun isInject(): Boolean {
        if (!config.enableHook) {
            return false
        }
        if (config.hookAll) {
            return true
        }
        return false
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        isInterface = (access and Opcodes.ACC_INTERFACE) != 0
        className =
            name.substring(name.lastIndexOf(File.separator) + 1) // Extract class name from the full path
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val isConstructor = "<init>" == name || "<clinit>" == name
        val isAbstractMethod = (access and Opcodes.ACC_ABSTRACT) != 0
        val isNativeMethod = (access and Opcodes.ACC_NATIVE) != 0

        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (isInterface || isAbstractMethod || isNativeMethod || isConstructor) {
            mv
        } else {
            MethodEnterAndExitAdapter(api, mv, access, name, descriptor, className)
        }
    }

    class MethodEnterAndExitAdapter(
        api: Int, mv: MethodVisitor,
        private val access: Int,
        private val methodName: String,
        private val desc: String,
        private val className: String
    ) : AdviceAdapter(api, mv, access, methodName, desc) {
        private var argsTypes: Array<Type> = Type.getArgumentTypes(desc)
        private var returnType: Type = Type.getReturnType(desc)
        private var isStatic = (access and Opcodes.ACC_STATIC) != 0

        // Called when the method is entered
        override fun onMethodEnter() {
            super.onMethodEnter()

            // Insert trace start logic
            println("======================  name = $className ===== $methodName")
            mv.visitLdcInsn(className)
            mv.visitLdcInsn(methodName)
            mv.visitMethodInsn(
                INVOKESTATIC,
                "com/ww/gradle/tracklib/MethodHookHandler",
                "enter",
                "(Ljava/lang/String;Ljava/lang/String;)V",
                false
            )
        }

        // Called when the method is exited
        override fun onMethodExit(opcode: Int) {
            super.onMethodExit(opcode)
            //即返回int、long、float、double、reference类型
            if ((opcode >= Opcodes.IRETURN && opcode < Opcodes.RETURN)) {
                mv.visitLdcInsn(className)
                mv.visitLdcInsn(methodName)
                mv.visitMethodInsn(
                    INVOKESTATIC, "com/ww/gradle/tracklib/MethodHookHandler",
                    "exit",
                    "(Ljava/lang/String;Ljava/lang/String;)V",
                    false
                )

                //即void类型返回
            } else if (opcode == RETURN) {
                mv.visitInsn(ACONST_NULL)
//                getArgs()
                mv.visitLdcInsn(className)
                mv.visitLdcInsn(methodName)
                mv.visitMethodInsn(
                    INVOKESTATIC, "com/ww/gradle/tracklib/MethodHookHandler",
                    "exit",
                    "(Ljava/lang/String;Ljava/lang/String;)V",
                    false
                )
            }
            // 重新生成返回指令
            mv.visitInsn(opcode)
        }

        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
            val newMaxStack = maxStack.coerceAtLeast(4)
            super.visitMaxs(newMaxStack, maxLocals)
        }

        override fun visitCode() {
            super.visitCode()
        }

        override fun visitInsn(opcode: Int) {
            super.visitInsn(opcode)
        }


        /**
         * 装载当前实例
         */
        private fun getArgs() {
            if (isStatic) {
                mv.visitInsn(ACONST_NULL)
            } else {
                mv.visitVarInsn(ALOAD, 0)
            }
//            mv.visitLdcInsn(className)//className
//            mv.visitLdcInsn(methodName)//methodbName
//            mv.visitLdcInsn(getArgsType())//argsTypes
//            mv.visitLdcInsn(returnType.className)//returntype

            getICONST(argsTypes.size)
//            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
            var valLen = 0
            for (i in argsTypes.indices) {
                mv.visitInsn(DUP)
                getICONST(i)
                getOpCodeLoad(argsTypes[i], if (isStatic) valLen else valLen + 1)
                mv.visitInsn(AASTORE)
                valLen += getlenByType(argsTypes[i])
            }

        }


        private fun getICONST(i: Int) {
            if (i == 0) {
                mv.visitInsn(ICONST_0)
            } else if (i == 1) {
                mv.visitInsn(ICONST_1)
            } else if (i == 2) {
                mv.visitInsn(ICONST_2)
            } else if (i == 3) {
                mv.visitInsn(ICONST_3)
            } else if (i == 4) {
                mv.visitInsn(ICONST_4)
            } else if (i == 5) {
                mv.visitInsn(ICONST_5)
            } else {
                mv.visitIntInsn(BIPUSH, i)
            }
        }

        /**
         * 类型处理
         * @param type
         * @param argIndex
         */
        private fun getOpCodeLoad(type: Type, argIndex: Int) {
            if (type.equals(Type.INT_TYPE)) {
                mv.visitVarInsn(ILOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Integer",
                    "valueOf",
                    "(I)Ljava/lang/Integer;",
                    false
                )
                return
            }
            if (type.equals(Type.BOOLEAN_TYPE)) {
                mv.visitVarInsn(ILOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
                return
            }
            if (type.equals(Type.CHAR_TYPE)) {
                mv.visitVarInsn(ILOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Character",
                    "valueOf",
                    "(C)Ljava/lang/Character;",
                    false
                )
                return
            }
            if (type.equals(Type.SHORT_TYPE)) {
                mv.visitVarInsn(ILOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Short",
                    "valueOf",
                    "(S)Ljava/lang/Short;",
                    false
                )
                return
            }
            if (type.equals(Type.BYTE_TYPE)) {
                mv.visitVarInsn(ILOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Byte",
                    "valueOf",
                    "(B)Ljava/lang/Byte;",
                    false
                )
                return
            }

            if (type.equals(Type.LONG_TYPE)) {
                mv.visitVarInsn(LLOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Long",
                    "valueOf",
                    "(J)Ljava/lang/Long;",
                    false
                )
                return
            }
            if (type.equals(Type.FLOAT_TYPE)) {
                mv.visitVarInsn(FLOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Float",
                    "valueOf",
                    "(F)Ljava/lang/Float;",
                    false
                )
                return
            }
            if (type.equals(Type.DOUBLE_TYPE)) {
                mv.visitVarInsn(DLOAD, argIndex)
                mv.visitMethodInsn(
                    INVOKESTATIC,
                    "java/lang/Double",
                    "valueOf",
                    "(D)Ljava/lang/Double;",
                    false
                )
                return
            }
            mv.visitVarInsn(ALOAD, argIndex)
        }

        /**
         * 获取参数类型的字符串表示
         */
        private fun getArgsType(): String {
            val iMax = argsTypes.size - 1
            if (iMax == -1) {
                return "[]"
            }

            val b = StringBuilder()
            b.append('[')

            for (i in 0..iMax) {
                b.append(argsTypes[i].className)
                if (i == iMax) {
                    b.append(']')
                    return b.toString()
                }
                b.append(", ")
            }

            return b.toString()
        }

        /**
         * 获取类型的长度
         */
        private fun getlenByType(type: Type): Int {
            return if (type.equals(Type.DOUBLE_TYPE) || type.equals(Type.LONG_TYPE)) {
                2
            } else {
                1
            }
        }
    }
}
