package com.taobao.android.builder.insant.visitor;

import com.taobao.android.builder.insant.ModifyClassFinder;
import com.android.build.gradle.internal.incremental.TBIncrementalVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

/**
 * 创建日期：2018/11/30 on 上午10:02
 * 描述:
 * 作者:zhayu.ll
 */
public class ModifyFieldVisitor extends FieldVisitor {


    private ModifyClassFinder.CodeChange py = new ModifyClassFinder.CodeChange();

    public ModifyFieldVisitor(int api, FieldVisitor fv, ModifyClassFinder.CodeChange patchPolicy) {
        super(api, fv);
        this.py = patchPolicy;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals(TBIncrementalVisitor.MODIFY_FIELD.getDescriptor()) && visible && py.getPy()!= ModifyClassFinder.PatchPolicy.ADD) {
            py.setPy(ModifyClassFinder.PatchPolicy.MODIFY);
        }else if (desc.equals(TBIncrementalVisitor.ADD_FIELD.getDescriptor()) && visible){
            throw new RuntimeException("add field is not support!");
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {

        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }
}
