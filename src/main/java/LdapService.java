import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LdapService {
	
    //Connexion et Fermeture
    private DirContext connect() throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, LdapConfig.LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, LdapConfig.ADMIN_DN);
        env.put(Context.SECURITY_CREDENTIALS, LdapConfig.ADMIN_PASSWORD);
        
        return new InitialDirContext(env);
    }
    
    private void close(DirContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }
    ///////////////////////////////////////////////////////////
    
    
    //Authentifier un Utilisateur
    
    public boolean authenticate(String uid, String password) {
        DirContext ctx = null;
        try {
            String userDn = "uid=" + uid + "," + LdapConfig.BASE_DN;
            
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, LdapConfig.LDAP_URL);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            
            ctx = new InitialDirContext(env);
            System.out.println("✓ Authentification réussie pour : " + uid);
            return true;
        } catch (AuthenticationException e) {
            System.out.println("✗ Échec d'authentification pour : " + uid);
            return false;
        } catch (NamingException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(ctx);
        }
    }
    ////////////////////////////////////////////////////////////////

    ///Rechercher un Utilisateur
    
    public void searchUser(String uid) {
        DirContext ctx = null;
        try {
            ctx = connect();
            
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            String filter = "(uid=" + uid + ")";
            NamingEnumeration<SearchResult> results = ctx.search(LdapConfig.BASE_DN, filter, controls);
            
            if (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                
                System.out.println("--- Utilisateur trouvé ---");
                System.out.println("DN: " + result.getNameInNamespace());
                System.out.println("CN: " + attrs.get("cn").get());
                System.out.println("Mail: " + attrs.get("mail").get());
            } else {
                System.out.println("Aucun utilisateur trouvé avec uid = " + uid);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            close(ctx);
        }
    }
    /////////////////////////////////////////////////////////7
    
    //Créer un Utilisateur
    
    public void createUser(String uid, String cn, String sn, String mail, String password) {
        DirContext ctx = null;
        try {
            ctx = connect();
            
            Attributes attrs = new BasicAttributes();
            Attribute objClass = new BasicAttribute("objectClass");
            objClass.add("inetOrgPerson");
            objClass.add("organizationalPerson");
            objClass.add("person");
            objClass.add("top");
            
            attrs.put(objClass);
            attrs.put("cn", cn);
            attrs.put("sn", sn);
            attrs.put("uid", uid);
            attrs.put("mail", mail);
            attrs.put("userPassword", password);
            
            String dn = "uid=" + uid + "," + LdapConfig.BASE_DN;
            ctx.createSubcontext(dn, attrs);
            
            System.out.println("✓ Utilisateur créé : " + dn);
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            close(ctx);
        }
    }
    ///////////////////////////////////////////////////////////
    
    
    ///Modifier l'Email
    
    public void updateEmail(String uid, String newEmail) {
        DirContext ctx = null;
        try {
            ctx = connect();
            
            String dn = "uid=" + uid + "," + LdapConfig.BASE_DN;
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail", newEmail));
            
            ctx.modifyAttributes(dn, mods);
            System.out.println("✓ Email mis à jour pour : " + uid);
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            close(ctx);
        }
    }
    ///////////////////////////////////////////////////////////
    
    //Supprimer un Utilisateur
    public void deleteUser(String uid) {
        DirContext ctx = null;
        try {
            ctx = connect();
            
            String dn = "uid=" + uid + "," + LdapConfig.BASE_DN;
            ctx.destroySubcontext(dn);
            
            System.out.println("✓ Utilisateur supprimé : " + uid);
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            close(ctx);
        }
    }
    
    
    
}