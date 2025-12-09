# LDAP (Light Weight Data Access Protocol)

## 📋 Vue d'ensemble
Ce TP vous permet d'apprendre à configurer un serveur LDAP, créer une arborescence d'annuaire et développer une application Java pour interagir avec LDAP.

---

## Partie 1 : Installation et Configuration du Serveur LDAP

### Étape 1 : Installation d'ApacheDS

1. **Télécharger ApacheDS**
   - Visitez le site officiel Apache Directory (section ApacheDS)
   - Téléchargez la version Windows Installer (.exe)

2. **Installer ApacheDS**
   - Exécutez l'installateur
   - Laissez les ports par défaut :
     - LDAP : `10389`
     - LDAPS : `10636`
   - L'installateur enregistre ApacheDS comme service Windows

3. **Vérifier le service**
   - Ouvrez le menu Démarrer
   - Tapez `services.msc`
   - Cherchez "ApacheDS -- default"
   - Vérifiez que l'état est "Running/En cours d'exécution"

### Étape 2 : Installation d'Apache Directory Studio

1. Téléchargez Apache Directory Studio depuis le site officiel
2. Installez l'application (client graphique pour gérer LDAP)

### Étape 3 : Créer une connexion LDAP

1. **Ouvrir Apache Directory Studio**
2. **Aller dans l'onglet LDAP**
3. **Clic droit → New Connection...**
4. **Configuration de la connexion :**
   - **Connection name** : `ApacheDS-Local`
   - **Hostname** : `localhost`
   - **Port** : `10389`
   - **Encryption** : No encryption

5. **Onglet Authentification :**
   - **Bind DN or user** : `uid=admin,ou=system`
   - **Bind password** : `secret` (mot de passe par défaut)
   - **Authentication method** : Simple

6. **Tester la connexion et valider**

---

## Partie 2 : Création de l'Arborescence LDAP (DIT)

### Étape 4 : Créer les Unités d'Organisation (OU)

1. **Dans Apache Directory Studio :**
   - Fichier → Nouveau → Navigateur LDAP → Fichier LDAP

2. **Créer un fichier LDIF avec le contenu suivant :**

```ldif
dn: ou=users,ou=system
objectClass: organizationalUnit
objectClass: top
ou: users

dn: ou=groups,ou=system
objectClass: organizationalUnit
objectClass: top
ou: groups
```

3. **Importer le fichier LDIF** dans Apache Directory Studio

### Étape 5 : Créer des Utilisateurs

**Créer un fichier LDIF pour ajouter plusieurs utilisateurs :**

```ldif
dn: uid=jdoe,ou=users,ou=system
objectClass: inetOrgPerson
objectClass: organizationalPerson
objectClass: person
objectClass: top
cn: John Doe
sn: Doe
uid: jdoe
mail: jdoe@example.com
userPassword: password123

dn: uid=asmith,ou=users,ou=system
objectClass: inetOrgPerson
objectClass: organizationalPerson
objectClass: person
objectClass: top
cn: Alice Smith
sn: Smith
uid: asmith
mail: asmith@example.com
userPassword: password456
```

**Importer ce fichier dans Apache Directory Studio**

### Étape 6 : Modifier un Utilisateur

**Créer un fichier LDIF de modification :**

```ldif
dn: uid=jdoe,ou=users,ou=system
changetype: modify
replace: mail
mail: john.doe@example.com
```

**Appliquer la modification via Apache Directory Studio**

### Étape 7 : Supprimer un Utilisateur

**Créer un fichier LDIF de suppression :**

```ldif
dn: uid=asmith,ou=users,ou=system
changetype: delete
```

**Appliquer la suppression**

---

## Partie 3 : Intégration Java avec JNDI

### Étape 8 : Structure du Projet Java

Créez un projet Java avec la structure suivante :

```
LdapJavaProject/
├── src/
│   └── main/
│       └── java/
│           ├── LdapConfig.java
│           ├── LdapService.java
│           └── LdapDemoApp.java
└── pom.xml (si Maven)
```

### Étape 9 : Classe LdapConfig

**Créez la classe `LdapConfig.java` :**

```java
public class LdapConfig {
    public static final String LDAP_URL = "ldap://localhost:10389";
    public static final String ADMIN_DN = "uid=admin,ou=system";
    public static final String ADMIN_PASSWORD = "secret";
    public static final String BASE_DN = "ou=users,ou=system";
}
```

### Étape 10 : Classe LdapService

**Créez la classe `LdapService.java` avec les méthodes suivantes :**

#### 10.1 Connexion et Fermeture

```java
import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LdapService {
    
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
```

#### 10.2 Authentifier un Utilisateur

```java
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
```

#### 10.3 Rechercher un Utilisateur

```java
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
```

#### 10.4 Créer un Utilisateur

```java
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
```

#### 10.5 Modifier l'Email

```java
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
```

#### 10.6 Supprimer un Utilisateur

```java
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
```

### Étape 11 : Programme Principal

**Créez la classe `LdapDemoApp.java` :**

```java
public class LdapDemoApp {
    public static void main(String[] args) {
        LdapService service = new LdapService();
        
        System.out.println("=== DÉMONSTRATION LDAP AVEC JNDI ===\n");
        
        // 1. Créer un utilisateur
        System.out.println("1. Création d'un utilisateur");
        service.createUser("bmartin", "Bob Martin", "Martin", "bmartin@example.com", "bobpass");
        
        // 2. Rechercher l'utilisateur
        System.out.println("\n2. Recherche de l'utilisateur");
        service.searchUser("bmartin");
        
        // 3. Authentification
        System.out.println("\n3. Test d'authentification");
        service.authenticate("bmartin", "bobpass");
        service.authenticate("bmartin", "wrongpass");
        
        // 4. Modifier l'email
        System.out.println("\n4. Modification de l'email");
        service.updateEmail("bmartin", "bob.martin@example.com");
        service.searchUser("bmartin");
        
        // 5. Supprimer l'utilisateur
        System.out.println("\n5. Suppression de l'utilisateur");
        service.deleteUser("bmartin");
        
        System.out.println("\n=== FIN DE LA DÉMONSTRATION ===");
    }
}
```

### Étape 12 : Exécution

1. **Assurez-vous qu'ApacheDS est démarré**
2. **Compilez le projet Java**
3. **Exécutez la classe `LdapDemoApp`**

**Résultat attendu en console :**

```
=== DÉMONSTRATION LDAP AVEC JNDI ===

1. Création d'un utilisateur
✓ Utilisateur créé : uid=bmartin,ou=users,ou=system

2. Recherche de l'utilisateur
--- Utilisateur trouvé ---
DN: uid=bmartin,ou=users,ou=system
CN: Bob Martin
Mail: bmartin@example.com

3. Test d'authentification
✓ Authentification réussie pour : bmartin
✗ Échec d'authentification pour : bmartin

4. Modification de l'email
✓ Email mis à jour pour : bmartin
--- Utilisateur trouvé ---
DN: uid=bmartin,ou=users,ou=system
CN: Bob Martin
Mail: bob.martin@example.com

5. Suppression de l'utilisateur
✓ Utilisateur supprimé : bmartin

=== FIN DE LA DÉMONSTRATION ===
```

---

## 🎯 Objectifs Atteints

✅ Configuration d'un serveur LDAP ApacheDS  
✅ Création d'une arborescence DIT  
✅ Gestion des utilisateurs via LDIF  
✅ Connexion LDAP en Java avec JNDI  
✅ Opérations CRUD complètes  
✅ Authentification des utilisateurs  
✅ Recherche par filtre LDAP

---

## 📝 Points Clés à Retenir

- **LDAP** est un protocole d'accès aux annuaires
- **DIT** (Directory Information Tree) est l'arborescence hiérarchique
- **DN** (Distinguished Name) identifie uniquement une entrée
- **LDIF** permet d'importer/exporter des données
- **JNDI** est l'API Java pour interagir avec LDAP
- Les opérations principales sont : bind, search, add, modify, delete
