package ca.tnt.ldaputils;

import ca.tnt.ldaputils.impl.LdapEntry;
import ca.tnt.ldaputils.impl.LdapOrganization;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  19/12/10 12:16 AM MST
 * <p/>
 * Modified : $Date$ UTC
 * <p/>
 * Revision : $Revision$
 *
 * @author Trenton D. Adams
 */
@SuppressWarnings(
    {"JavaDoc", "ClassWithoutConstructor", "PublicMethodNotExposedInInterface"})
@RunWith(FrameworkRunner.class)
@CreateLdapServer(
    transports =
        {
            @CreateTransport(protocol = "LDAP")
        })
@CreateDS(allowAnonAccess = false, name = "example-partition",
    partitions =
        {
            @CreatePartition(
                name = "example",
                suffix = "dc=example,dc=com",
                contextEntry = @ContextEntry(
                    entryLdif = "dn: dc=example,dc=com\n" +
                        "objectclass: dcObject\n" +
                        "objectclass: organization\n" +
                        "o: Example\n" +
                        "dc: example"))
        })
@ApplyLdifFiles({
    "example.schema.ldif",
    "add-domain.ldif",
    "add-person.ldif"})
public class PersonTest extends AbstractLdapTestUnit
{
    private LdapManager manager;

    @Before
    public void setUp()
    {
        manager = new LdapManager("localhost", "" + ldapServer.getPort(),
            "uid=admin,ou=system", "secret");
    }

    @Test
    public void testLdapOrg()
        throws NamingException, NoSuchAlgorithmException
    {
        final LdapName ldapName = new LdapName(
            "uid=iop,ou=People,dc=example,dc=com");
        final LdapEntry ldapEntry = (LdapEntry) manager.find(
            LdapEntry.class, ldapName);
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final byte[] thedigest = md.digest(
            (byte[]) ldapEntry.getAttributes().get("jpegPhoto").get());
        final BigInteger bigInt = new BigInteger(1, thedigest);
        String hashtext = bigInt.toString(16);

        while (hashtext.length() < 32)
        {  // Now we need to zero pad it if you actually want the full 32 chars.
            hashtext = "0" + hashtext;
        }
        Assert.assertEquals("md5", "c000a2b462f78d5e7d22c43eb7d346cc",
            hashtext);
    }

}
