
# 📚 PagePals

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Angular](https://img.shields.io/badge/Angular-17-red?logo=angular)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)
![Build](https://img.shields.io/badge/build-passing-brightgreen)

**PagePals** est une application web qui permet aux passionné(e)s de lecture de trouver, rejoindre ou créer des cercles littéraires, en ligne ou en présentiel. Le but est de faciliter les échanges autour des livres et de favoriser les rencontres entre lecteurs et lectrices partageant des goûts similaires.

---

## 🚀 Objectifs

- Offrir une plateforme conviviale pour organiser ou rejoindre des cercles de lecture.
- Permettre une gestion simple des rencontres littéraires, avec une dimension sociale.
- Encourager la lecture et les discussions autour des livres dans un cadre communautaire.

---

## 🛠️ Stack technique

- **Back-end** : Java 21, Spring Boot, Spring Security, Spring Data JPA
- **Front-end** : Angular
- **Base de données** : MySQL
- **Outils** : Maven, Git, GitHub

---

## 👤 Rôles utilisateurs

- **Administrateur** :
  - Gestion des utilisateurs
  - Gestion des cercles
  - Modération (fonctionnalité prévue)
  
- **Membre** :
  - Création, modification et suppression de ses propres cercles
  - Inscription / désinscription à une rencontre
  - Proposition d’un livre (si créateur du cercle)
  - Accès à son profil avec cercles créés et rejoints

---

## ✨ Fonctionnalités principales

- Authentification : inscription, connexion, déconnexion
- Création d’un cercle littéraire : lieu (en ligne ou présentiel), date, description, nombre de places
- Consultation et recherche de cercles avec filtres (lieu, date, genre…)
- Fiche détaillée d’un cercle
- Participation à une rencontre
- Profil utilisateur : pseudo, photo, bio, date d’inscription, cercles créés et rejoints

---

## 🔒 Fonctionnalités à venir (version ultérieure)

- Notifications
- Récurrence des rencontres
- Historique de lectures
- Outils de modération

---

## ⚙️ Installation et exécution locale

### 1. Cloner le projet

```bash
git clone https://github.com/ton-utilisateur/pagepals.git
cd pagepals
```

### 2. Configuration du back-end (Spring Boot)

Dans `src/main/resources/application.properties`, configurer ta base MySQL :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pagepals
spring.datasource.username=ton_user
spring.datasource.password=ton_mdp
spring.jpa.hibernate.ddl-auto=update
```

### 3. Lancer le back-end

```bash
./mvnw spring-boot:run
```

Par défaut, l’API sera disponible sur `http://localhost:8080`.

### 4. Lancer le front-end (Angular)

```bash
cd frontend
npm install
ng serve
```

Le front sera accessible sur `http://localhost:4200`.

---

## ✍️ Auteure

Projet réalisé par Servane Beaudier dans le cadre de la formation **Conceptrice Développeuse d’Applications** (2025).
