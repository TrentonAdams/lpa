package ca.tnt.ldaputils.ldapimpl;

import ca.tnt.ldaputils.annotations.LdapAttribute;
import ca.tnt.ldaputils.annotations.LdapEntity;
import ca.tnt.ldaputils.impl.LdapEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  03/01/11 5:09 PM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings({"PublicMethodNotExposedInInterface"})
@LdapEntity(
    requiredObjectClasses = {"inetOrgPerson", "organizationalPerson", "person"})
public class InetOrgPerson extends LdapEntry
{
    @LdapAttribute(name = "jpegPhoto")
    private List<byte[]> jpegPhotos;

    /**
     * initializes an empty jpegPhotos
     */
    public InetOrgPerson()
    {
        jpegPhotos = new ArrayList<byte[]>(10);
    }

    /**
     * List of all the user's jpeg photos.
     *
     * @return the jpeg photos
     */
    public List<byte[]> getJpegPhotos()
    {
        return Collections.unmodifiableList(jpegPhotos);
    }
}
