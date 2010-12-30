package ca.tnt.ldaputils.annotations;

/**
 * Defines the types of LDAP objectClass(es).  There are essentially two types:
 * {@link #STRUCTURAL}, and {@link #AUXILIARY}.
 * <p/>
 * Created :  10-Dec-2010 4:00:38 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
public enum ObjectClassType
{
    /**
     * An Auxiliary ldap objectclass is just an add-on to the entry.  For
     * example, you might define an ldap entry as having the objectClass of
     * "labeledURIObject", which simply means that entry can now accept web site
     * urls with a space separated description.
     */
    AUXILIARY,

    /**
     * a structural ldap objectClass is one that provides the base attribute
     * definitions for an ldap entry.  It can be supplemented by an {@link
     * #AUXILIARY} objectClass, as defined in {@link #AUXILIARY}.  Or, it can be
     * a hierarchy of structural objectClass(es).  A good example of this type
     * of objectClass is the "person" objectClass.  A good example of
     * hierarchical structural objectClasses are the inetOrgPerson ->
     * organizationalPerson -> person object classes, where person is the base
     * most class.
     */
    STRUCTURAL
}
