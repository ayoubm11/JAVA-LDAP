

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