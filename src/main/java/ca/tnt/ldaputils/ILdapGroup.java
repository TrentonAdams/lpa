/*
 * LDAPGroup.java
 *
 * Created on April 14, 2006, 6:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ca.tnt.ldaputils;

import javax.naming.InvalidNameException;
import java.util.Map;
import java.util.SortedSet;

/**
 * @author trenta
 */
public interface ILdapGroup extends ILdapEntry
{
    /**
     * TODO : Implement a getMembers(member type)
     * <p/>
     * TODO : Change the default return type to LDAP_GROUP
     *
     * @param objectType
     *
     * @return map of members with organization as the key.  The members are of
     *         type @link{LDAPFactory.LDAP_TNT_BUSINESS} @param keyAttribute
     */
    Map getMembers(String keyAttribute, int objectType)
        throws InvalidNameException;

    /**
     * Add a member to this group.
     *
     * @param ldapEntry the ldap object corresponding to the member to add.
     */
    void addMember(ILdapEntry ldapEntry);

    /**
     * Remove a member from this group.
     *
     * @param ldapEntry the ldap object corresponding to the member to add.
     */
    void removeMember(ILdapEntry ldapEntry);

    /**
     * Returns a non-null SortedSet of group members.  If there are no members,
     * the set's size is ZERO.
     *
     * @return all members in DN format, in alphabetical order.
     */
    SortedSet getMembers();
}
