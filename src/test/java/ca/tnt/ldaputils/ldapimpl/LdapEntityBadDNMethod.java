package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.annotations.DN;
import ca.tnt.ldaputils.annotations.LdapEntity;

import javax.naming.ldap.LdapName;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  23-Aug-2010 1:26:35 AM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@LdapEntity
public class LdapEntityBadDNMethod
{
    @DN
    protected LdapName dn;

    public void getDn()
    {

    }
}
