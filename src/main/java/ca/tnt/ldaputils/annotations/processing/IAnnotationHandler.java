/**
 * This file is part of the Ldap Persistence API (LPA).
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
package ca.tnt.ldaputils.annotations.processing;

import ca.tnt.ldaputils.LdapManager;

import java.lang.annotation.Annotation;

/**
 * Annotation callback handler.  Implement this when you want to process
 * annotations recursively through the entire tree of subclasses.
 * <p/>
 * Created :  21-Aug-2010 11:51:11 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
public interface IAnnotationHandler
{
    /**
     * Process the annotation on the given field.
     *
     * @param annotation the annotation
     * @param field      the field to process
    void processAnnotation(Annotation annotation, Field field);

    /**
     * Process the annotation on the given method.
     *
     * @param annotation the annotation
     * @param method     the field to process
    void processAnnotation(Annotation annotation, Method method);
     */

    /**
     * Process the annotation on the given annotatedClass.  This is a callback
     * to actually do something with the annotation.
     * <p/>
     * it is recommended that the implementing object keeps track of problems
     * and throws a RuntimeException in the {@link #validateProcessing()} method
     * if something went wrong.
     *
     * @param annotation     the annotation
     * @param annotatedClass the field to process
     *
     * @return true if annotation processing was successful
     *
     * @throws RuntimeException The implementor may throw a RuntimeException
     *                          from this method.  It is recommended that this
     *                          does not occur, unless it is severe.  First see
     *                          {@link #validateProcessing()}
     */
    boolean processAnnotation(final Annotation annotation,
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
     * This is the primary Class annotation that this handler expects to be on
     * the Class, indicating it supports the Class being processed.
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

    /**
     * Method for validating the processing of the annotations.  This is called
     * after all classes in the class tree have been traversed and processed.
     * This is the final validation, and is meant for problems that could not be
     * detected until traversing the entire class tree.  For example, it may be
     * that you require a particular annotation on at least one class in the
     * hierarchy, but your handler doesn't know when the end of Class hierarchy
     * traversal is complete; this method is called after traversal completion.
     *
     * @throws RuntimeException if it is determined that a processing error has
     *                          occurred that could not be determined until
     *                          class hierarchy traversal is complete
     */
    void validateProcessing();

    /**
     * Sets the established ldap manager object, which should be
     * pre-authenticated.
     *
     * @param managerInstance the already authenticated manager
     */
    void setManager(final LdapManager managerInstance);
}
