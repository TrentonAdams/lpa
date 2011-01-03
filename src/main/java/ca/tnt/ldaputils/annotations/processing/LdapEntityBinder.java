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

import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * {@link IAnnotationHandler} implementation that processes LPA annotations for
 * the purpose of generating JNDI Attributes for the bind call.
 * <p/>
 * Created :  02/01/11 1:09 PM MST
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"ClassWithoutNoArgConstructor"})
public class LdapEntityBinder extends LdapEntityLoader
    implements IAnnotationHandler
{
    private LdapName dn;

    /**
     * Initializes the entity binder with the instance to be bound to ldap.
     *
     * @param entityInstance the {@link LdapEntity} annotated entityInstance
     *                       instance that needs to be bound to ldap.
     */
    public LdapEntityBinder(final Object entityInstance)
    {
        entity = entityInstance;
        attributes = new BasicAttributes();
    }

    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected boolean validateObjectClasses(final LdapEntity annotation)
    {
        return true;
    }

    private Attributes attributes;

    /**
     * Does nothing, and does not call the super, as the entity binder does not
     * require the handling of manager instances.
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected void processManager(final Field field)
        throws IllegalAccessException
    {
    }

    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected void processLdapAttribute(final Class annotatedClass,
        final Field field) throws IllegalAccessException, NamingException,
        InvocationTargetException, NoSuchMethodException, InstantiationException
    {
        final LdapAttribute attrAnnotation = field.getAnnotation(
            LdapAttribute.class);
        final String attrName = attrAnnotation.name();
        final String referenceDNMethod = attrAnnotation.referencedDNMethod();
        final Class<?> aggClass = attrAnnotation.aggregateClass();
        final Class refType = field.getType();

        field.setAccessible(true);
        System.out.println("attribute field: " + field.getName() + ": " +
            field.get(entity));
        field.setAccessible(false);
    }

    /**
     * Only calls {@link LdapEntityLoader#validateDN(Class, Field)}
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected void processDN(final Class annotatedClass, final Field field)
        throws IllegalAccessException, NoSuchMethodException
    {
        validateDN(annotatedClass, field);
        field.setAccessible(true);
        dn = (LdapName) field.get(entity);
        field.setAccessible(false);
    }

    @Override
    public void validateProcessing()
    {
    }

    /**
     * Retrieve the processed attributes for Ldap.
     *
     * @return the directory Attributes to be bound
     */
    @SuppressWarnings({"PublicMethodNotExposedInInterface"})
    public Attributes getAttributes()
    {
        return attributes;
    }

    /**
     * Returns the dn processed during {@link #processDN(Class, Field)}
     *
     * @return the dn
     */
    @SuppressWarnings({"PublicMethodNotExposedInInterface"})
    public LdapName getDn()
    {
        return dn;
    }
}
