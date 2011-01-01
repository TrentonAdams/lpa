package ca.tnt.ldaputils;

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
import javax.naming.ldap.LdapName;
import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Created :  31/12/10 10:30 PM MST
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
            @CreateTransport(protocol = "LDAP", port = 8389)
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
    "add-busgroups.ldif",
    "add-businesses.ldif"})
public class PropertyLoadTest extends AbstractLdapTestUnit
{
    private LdapManager manager;

    @Before
    public void setUp()
    {
        manager = new LdapManager();
    }

    @Test
    public void testLdapOrg() throws InvalidNameException
    {
        final LdapName ldapName = new LdapName(
            "o=Pulp Mill.,ou=businesses,dc=example,dc=com");
        final LdapOrganization ldapEntry = (LdapOrganization) manager.find(
            LdapOrganization.class, ldapName);
        Assert.assertEquals("Organization should be Pulp Mill",
            "Pulp Mill.", ldapEntry.getOrganization());
    }


}
