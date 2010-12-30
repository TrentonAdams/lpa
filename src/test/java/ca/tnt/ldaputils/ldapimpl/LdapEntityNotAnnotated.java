package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.impl.LdapEntry;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  23-Aug-2010 1:35:37 AM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
public class LdapEntityNotAnnotated extends LdapEntry
{
    @LdapAttribute(name = "member")
    protected SortedSet sortedMembers;

    @LdapAttribute(name="cn")
    protected String businessCategory;
}
