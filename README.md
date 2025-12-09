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

1. **Créer un fichier LDIF avec le contenu suivant :**

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

2. **Importer le fichier LDIF** dans Apache Directory Studio

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

### Étape 9 : Exécution

1. **Assurez-vous qu'ApacheDS est démarré**
2. **Compilez le projet Java**
3. **Exécutez la classe `LdapDemoApp`**



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
