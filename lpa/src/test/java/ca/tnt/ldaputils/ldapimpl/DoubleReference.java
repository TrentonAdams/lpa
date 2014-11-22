package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.ILdapGroup;
import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.impl.LdapEntry;
import ca.tnt.ldaputils.impl.LdapGroup;

import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  03/10/12 5:29 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@LdapEntity
public class DoubleReference extends LdapEntry
{
    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDN = "${LdapOrganization.categoryDN}",
        referencedDNMethod = "getReferenceDN"
        /*"cn=?,ou=bus-categories,dc=example,dc=com"*/
    )
    private SortedMap<String, ILdapGroup> businessCategories;

}
