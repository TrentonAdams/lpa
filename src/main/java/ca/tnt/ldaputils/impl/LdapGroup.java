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
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 * <p/>
 * <p/>
 * TODO : When removing members, check for existence, then fail silently if it
 * does not exist.  Otherwise, remove it.
 *
 * @author Trenton D. Adams <trenta@athabascau.ca>
 */
@LdapEntity
public class LdapGroup extends LdapEntry
    implements ILdapGroup, Serializable, Comparable
{
    // REQUIRED_FEATURE replace with SortedMap of member entries
    /**
     * SortedSet of members of the group, in DN String format.
     */
    @LdapAttribute(name = "member")
    protected final SortedSet<String> sortedMembers;

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
}
