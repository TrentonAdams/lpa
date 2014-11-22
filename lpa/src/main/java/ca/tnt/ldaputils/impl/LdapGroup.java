/**
 * This file is part of the LDAP Persistence API (LPA).
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
package ca.tnt.ldaputils.impl;

import ca.tnt.ldaputils.ILdapEntry;
import ca.tnt.ldaputils.LdapManager;
import ca.tnt.ldaputils.ILdapGroup;
import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import org.apache.commons.lang.builder.*;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import java.io.Serializable;
import java.util.*;

/**
 * LDAPGroup implementation.
 * <p/>
 * Created :  14-Apr-2006 6:57:16 PM MST
 * <p/>
 * MINOR : When removing members, check for existence, then fail silently if it
 * does not exist.  Otherwise, remove it.
 *
 * @author Trenton D. Adams <trenta@athabascau.ca>
 */
@LdapEntity
public class LdapGroup extends LdapEntry
    implements ILdapGroup, Serializable, Comparable
{

    /**
     * REQUIRED_FEATURE replace with SortedMap of ILdapOrganization entries,
     * once we have gotten around infinite recursion.  However, this should
     * probably be done in an example object in the test package, not here, as
     * we do not necessarily want to link back to the original object.  Then
     * again, maybe we should simply be creating two separate sub projects.  One
     * for common Classes, doing it the way we "think", and one that is the
     * actual framework.
     * <p/>
     * SortedSet of members of the group, in DN String format.
     */
    @LdapAttribute(name = "member")
    protected final SortedSet<String> sortedMembers;

    @LdapAttribute(name = "businessCategory")
    private final SortedSet<String> businessCategories;

/*    public LdapGroup(final Attributes attributes, final LdapName dn)
        throws NamingException
    {
        super(attributes, dn);

        verifyObjectClass();
        populateMemebrs();
    }*/

    public LdapGroup()
    {
        super();
        sortedMembers = new TreeSet<String>();
        businessCategories = new TreeSet<String>();
    }

/*    private void populateMemebrs()
    {
        final List members;
        members = getAttributeValues("member");
        sortedMembers = new TreeSet(members);
        businessCategory = getStringValue("businessCategory");
    }

    private void verifyObjectClass() throws NamingException
    {
        if (!(isObjectClass("groupOfNames") ||
            isObjectClass("groupOfUniqueNames") ||
            isObjectClass("tntGroupOfNames")))
        {
            throw new ObjectClassNotSupportedException(
                objectClasses.toString() +
                    " must be one of [tntGroupOfNames, groupOfNames, groupOfUniqueNames]");
        }
    }*/

    public Map getMembers(final String keyAttribute, final int objectType)
        throws InvalidNameException
    {
        final Iterator memberIt;
        final Map members;

        final LdapManager manager = new LdapManager();
        members = new TreeMap();
        memberIt = sortedMembers.iterator();
        while (memberIt.hasNext())
        {
            final String member;
            member = (String) memberIt.next();
            final ILdapEntry ldapEntry =
                (ILdapEntry) manager.find(LdapEntry.class, new LdapName(
                    member));
            if (ldapEntry != null)
            {
                members.put(ldapEntry.getStringValue(keyAttribute), ldapEntry);
            }
        }

        return members;
    }

    @Override
    public SortedSet getMembers()
    {
        return Collections.unmodifiableSortedSet(sortedMembers);
    }

    public void addMember(final ILdapEntry ldapEntry)
    {
        modifyBatchAttribute(ILdapEntry.ADD_ATTRIBUTE, "member",
            ldapEntry.getDn().toString());
    }

    public void removeMember(final ILdapEntry ldapEntry)
    {
        modifyBatchAttribute(ILdapEntry.REMOVE_ATTRIBUTE, "member",
            ldapEntry.getDn().toString());
    }

    @SuppressWarnings({"ChainedMethodCall"})
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
    }

    @SuppressWarnings({"ChainedMethodCall", "NonFinalFieldReferenceInEquals"})
    @Override
    public boolean equals(final Object o)
    {
        final LdapGroup rhs = (LdapGroup) o;

        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(sortedMembers, rhs.sortedMembers)
            .isEquals();
    }

    @SuppressWarnings(
        {"ChainedMethodCall", "NonFinalFieldReferencedInHashCode"})
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).
            appendSuper(super.hashCode()).
            append(sortedMembers).
            toHashCode();
    }

    @SuppressWarnings({"ChainedMethodCall", "CompareToUsesNonFinalVariable"})
    @Override
    public int compareTo(final Object o)
    {
        final LdapGroup myClass = (LdapGroup) o;
        return new CompareToBuilder()
            .appendSuper(super.compareTo(o))
            .append(sortedMembers, myClass.sortedMembers)
            .toComparison();
    }

    /**
     * Retrieves a list of business categories for this group.
     *
     * @return a SortedSet of category names
     */
    @Override
    public SortedSet<String> getBusinessCategories()
    {
        return Collections.unmodifiableSortedSet(businessCategories);
    }
}
