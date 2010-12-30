package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.impl.LdapEntry;
import ca.tnt.ldaputils.impl.LdapGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class for testing alternate methods of aggregation that are not used in the
 * primary ldap entry classes provided by this library.
 * <p/>
 * Created :  21/12/10 11:18 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({
    "PublicMethodNotExposedInInterface", "JavaDoc",
    "ReturnOfCollectionOrArrayField", "ZeroLengthArrayAllocation"})
@LdapEntity
public class AlternateAggregates extends LdapEntry
{
    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private final SortedSet<LdapGroup> sortedSetGroups;

    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private LdapGroup singleGroup;

    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private final LdapGroup[] arrayOfGroups;

    @LdapAttribute(name = "businessCategory", aggregateClass = LdapGroup.class,
        referencedDNMethod = "getCategoryDN"
    )
    private final List<LdapGroup> listOfGroups;

    public AlternateAggregates()
    {
        sortedSetGroups = new TreeSet<LdapGroup>();
        arrayOfGroups = new LdapGroup[0];
        listOfGroups = new ArrayList<LdapGroup>();
    }

    /**
     * returns the category dn with a bind variable.
     *
     * @return the dn with a bind variable.
     */
    @SuppressWarnings(
        {"PublicMethodNotExposedInInterface", "MethodMayBeStatic"})
    public String getCategoryDN()
    {
        return "cn=?,ou=bus-categories,dc=example,dc=com";
    }

    public SortedSet<LdapGroup> getSortedSetGroups()
    {
        return sortedSetGroups;
    }

    public LdapGroup getSingleGroup()
    {
        return singleGroup;
    }

    public LdapGroup[] getArrayOfGroups()
    {
        return arrayOfGroups;
    }

    public List<LdapGroup> getListOfGroups()
    {
        return listOfGroups;
    }
}
