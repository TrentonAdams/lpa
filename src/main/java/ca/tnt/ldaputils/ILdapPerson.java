/*
 * LDAPPerson.java
 *
 * Created on April 14, 2006, 6:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ca.tnt.ldaputils;

/**
 *
 * @author trenta
 */
public interface ILdapPerson extends ILdapEntry
{
    public String getUID();
    public String getEMail();
}
