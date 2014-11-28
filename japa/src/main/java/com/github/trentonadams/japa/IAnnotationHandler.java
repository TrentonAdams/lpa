/**
 * This file is part of the LDAP Persistence API (LPA).
 *
 * Copyright Trenton D. Adams <lpa at trentonadams daught ca>
 *
 * LPA is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * LPA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with LPA.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See the COPYING file for more information.
 */
package com.github.trentonadams.japa;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Annotation callback handler.  Implement this when you want to process
 * annotations recursively through the entire tree of subclasses.
 * <p/>
 * TODO perhaps make each handler have a getFieldAnnotations() and a
 * getMethodAnnotations().  If we do, we'll need to finish the calls for field
 * and method processing.  We should also set the fields and methods as
 * accessible before, and inaccessible afterwards.
 * <p/>
 * Created :  21-Aug-2010 11:51:11 PM MST
 *
 * @author Trenton D. Adams
 */
public interface IAnnotationHandler
{
    /**
     * Process the annotation on the given field.
     *
     * @param annotation     the annotation
     * @param annotatedClass the class to process
     * @param field          the field to process
     */
    void processAnnotation(Annotation annotation, Class annotatedClass,
        Field field);

    /**
     * Process the annotation on the given method.
     *
     * @param annotation     the annotation
     * @param annotatedClass the class to process
     * @param method         the field to process
     */
    void processAnnotation(Annotation annotation, Class annotatedClass,
        Method method);

    /**
     * Process the annotation on the given package
     *
     * @param annotation     the annotation
     * @param annotatedClass the class to process
     * @param javaPackage    the package to process
     */
    void processAnnotation(Annotation annotation, Class annotatedClass,
        Package javaPackage);

    /**
     * Process the annotation on the given constructor.
     *
     * @param annotation     the annotation
     * @param annotatedClass the class to process
     * @param constructor    the constructor to process
     */
    void processAnnotation(Annotation annotation, Class annotatedClass,
        Constructor constructor);

    /**
     * Process the annotation on the given annotatedClass.  This is a callback
     * to actually do something with the annotation.  Generally speaking, the
     * implementing class should NOT deal with annotations on methods, packages,
     * fields, or constructors, by itself.  It should only deal with the class
     * level annotations.  The other methods will be called appropriately.
     * Namely {@link #processAnnotation(java.lang.annotation.Annotation, Class,
     * java.lang.reflect.Constructor)}, {@link #processAnnotation(java.lang.annotation.Annotation,
     * Class, java.lang.reflect.Field)}, {@link #processAnnotation(java.lang.annotation.Annotation,
     * Class, java.lang.reflect.Method)}, and {@link #processAnnotation(java.lang.annotation.Annotation,
     * Class, Package)}
     * <p/>
     * It is recommended that the implementing object throws a RuntimeException
     * if it can determine something went wrong up front.  In the case where it
     * cannot be determined if there's a problem with the object, until all
     * processing is complete, we recommend throwing the RuntimeException from
     * within the {@link #complete()} method.
     * <p/>
     * Issues that could be wrong are things like improper use of annotations.
     * Things like a class level annotation being present but no required field
     * level annotations.
     *
     * @param annotation     the annotation
     * @param annotatedClass the class to process
     *
     * @return true if annotation processing was successful
     *
     * @throws RuntimeException The implementer may throw a RuntimeException
     *                          from this method.  It is recommended that this
     *                          does not occur, unless it is severe.  First see
     *                          {@link #complete()}
     */
    void processAnnotation(final Annotation annotation,
        final Class annotatedClass);

    /**
     * It is expected that this handler has a reference to the object it wants
     * to operate on.  The {@link AnnotationProcessor } also needs access to
     * this object in order to determine annotations on it.
     *
     * @return the object to traverse the Class tree for.
     */
    Class getAnnotatedClass();

    /**
     * This is the primary Annotation (with target {@link ElementType#TYPE})
     * that this handler expects the annotated Class to be annotated with. If
     * the annotated class is annotated with this annotation, this indicates
     * that the handler supports the Class being processed.  If your handler
     * does not support the Class being processed, the handler will never be
     * called.
     *
     * @return the annotation class that this handler supports
     */
    Class<? extends Annotation> getAnnotationClass();

    /**
     * Called if no annotation on a particular class exists.  The handler can do
     * what it wants with it.  It may be that we need annotated classes at only
     * certain levels of the object tree, but not others.  So, the handler
     * could, for example, require that only the top level object has
     * annotations, while the super classes do not need them.  Or, perhaps it
     * requires it the other way around, for some reason.
     *
     * @param annotatedClass the class, in the object tree of {@link
     *                       #getAnnotatedClass}, that the annotation was NOT
     *                       found on.
     */
    void noAnnotation(final Class annotatedClass);

    void classAnnotationsComplete(final Class annotatedClass);

    /**
     * Method for notifying implementing class that the traversal of the entire
     * class hierarchy is complete.  This is called after all classes in the
     * class tree have been traversed and processed. This is the final
     * validation, and is meant for problems that could not be detected until
     * traversing the entire class tree.
     * <p/>
     * For example, it may be that you require a particular annotation on at
     * least one class in the hierarchy, but your handler doesn't know when the
     * end of Class hierarchy traversal is complete; this method is called after
     * traversal completion.
     *
     * @throws RuntimeException if it is determined that a processing error has
     *                          occurred that could not be determined until
     *                          class hierarchy traversal is complete
     */
    void complete();
}
