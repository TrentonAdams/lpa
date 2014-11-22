package ca.tnt.ldaputils;

/**
 * Represent the LDAP labeledURIObject objectClass.  URI objects are in the form
 * "http://example.com Example Site" where there is at least one space between
 * the URI and the label.  This interface splits the attribute into it's logical
 * components of "URI" and "Label".  No further javadoc on individual methods is
 * required.
 * <p/>
 * Created :  05/01/11 10:25 PM MST
 * <p/>
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"JavaDoc"})
public interface ILabeledURI
{
    String getURI();

    String getLabel();

    String setURI();

    String setLabel();
}
