package ca.tnt.ldaputils.annotations;

import ca.tnt.ldaputils.LdapManager;

import javax.naming.ldap.LdapName;
import java.lang.annotation.*;

/**
 * A field annotated with this is indicating that it is an LdapName variable for
 * storing the fully qualified distinguished name.  This is a special case for
 * an LDAP attribute, as the DN is REQUIRED for storage and what not.  So, a
 * field and a method must be defined.  Usually the DN that was used to find the
 * object, is injected here.  The alternative is the DN that was found during a
 * {@link LdapManager#search(LdapName, String, String, String[])} operation.
 *
 * The method name MUST be of the form "getPropetyX", where the first letter
 * of the field name is capitalized after "get".  So, in this case, the
 * member field is "propertyX".
 * <p/>
 * Created :  16-Aug-2010 8:06:13 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 * <p/>
 *
 * @author Trenton D. Adams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DN
{
}
