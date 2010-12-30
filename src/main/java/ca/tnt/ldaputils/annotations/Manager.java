package ca.tnt.ldaputils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * We use this as an annotation indicating you want the LdapManager that was
 * used to create this entry, injected into your code.  We probably do want
 * this, so that we can connect to multiple ldap servers at the same time, with
 * different objects using different factory instances.
 * <p/>
 * Created :  16-Aug-2010 9:07:25 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Manager
{
}
