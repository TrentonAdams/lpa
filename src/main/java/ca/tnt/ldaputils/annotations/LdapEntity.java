package ca.tnt.ldaputils.annotations;

import ca.tnt.ldaputils.impl.LdapEntry;

import java.lang.annotation.*;

/**
 * If you annotate with this, your POJO MUST contain an @DN annotation as well.
 * <p/>
 * It is recommended, though not required, that you inherit from {@link
 * LdapEntry}, as it covers a lot of functionality that is useful for updating
 * LDAP.  If you do not plan on doing updates to LDAP, then there is no added
 * benefit when inheriting from LdapEntry, and a simple POJO will suit your
 * needs just fine.
 * <p/>
 * Created :  16-Aug-2010 7:26:38 PM MST
 *
 * @author Trenton D. Adams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LdapEntity
{
    /**
     * The object classes that must be present for loading of this object to be
     * successful.
     * <p/>
     * e.g. organization, labeledURIObject, person, inetOrgPerson,
     * organizationalPerson, etc
     *
     * @return the String array of objectClasses
     */
    String[] requiredObjectClasses() default {};
}
