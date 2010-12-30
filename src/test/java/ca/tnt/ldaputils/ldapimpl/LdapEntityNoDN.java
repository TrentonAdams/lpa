package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.annotations.LdapEntity;

import javax.naming.ldap.LdapName;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  23-Aug-2010 1:53:22 AM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@LdapEntity
public class LdapEntityNoDN
{
    // not annotated, should fail
    protected LdapName dn;
}
