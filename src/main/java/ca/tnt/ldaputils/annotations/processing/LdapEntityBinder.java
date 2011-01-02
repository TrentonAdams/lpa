package ca.tnt.ldaputils.annotations.processing;

import ca.tnt.ldaputils.annotations.LdapEntity;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  02/01/11 1:09 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
public class LdapEntityBinder extends LdapEntityLoader
    implements IAnnotationHandler
{
    /**
     * Initialize
     */
    public LdapEntityBinder()
    {
    }

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

    @Override
    protected boolean validateObjectClasses(LdapEntity annotation)
    {
        return true;
    }

    private Attributes attributes;

    @Override
    protected void processLdapAttribute(final Class annotatedClass,
        final Field field) throws IllegalAccessException, NamingException,
        InvocationTargetException, NoSuchMethodException, InstantiationException
    {
        field.setAccessible(true);
        System.out.println("attribute field: " + field.getName() + ": " +
            field.get(entity));
        field.setAccessible(false);
    }

    @Override
    protected void processDN(final Class annotatedClass, final Field field)
        throws IllegalAccessException, NoSuchMethodException
    {
        field.setAccessible(true);
        System.out.println("dn field: " + field.getName());
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
    public Attributes getAttributes()
    {
        return attributes;
    }
}
