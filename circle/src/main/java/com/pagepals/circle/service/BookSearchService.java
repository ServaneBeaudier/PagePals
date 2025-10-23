package com.pagepals.circle.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pagepals.circle.dto.BookDTO;
import com.pagepals.circle.exception.BookNotFoundException;
import com.pagepals.circle.exception.ExternalApiException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Service responsable de la recherche de livres via l'API Google Books.
 * 
 * Permet de récupérer des informations bibliographiques (titre, auteurs, genre, couverture, etc.)
 * à partir d’un critère de recherche fourni par l’utilisateur.
 */
@Service
public class BookSearchService {

    /** Client HTTP utilisé pour les appels à l’API Google Books. */
    private final RestTemplate restTemplate = new RestTemplate();

    /** Clé d’authentification de l’API Google Books, chargée depuis le fichier .env. */
    private final String apiKey;

    /**
     * Initialise le service et charge la clé API depuis le fichier .env.
     * 
     * @throws IllegalStateException si la clé API n'est pas trouvée
     */
    public BookSearchService() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir") + "/circle")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        this.apiKey = dotenv.get("GOOGLE_BOOKS_API_KEY");

        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("Clé API Google Books non trouvée dans .env");
        }
    }

    /**
     * Recherche des livres correspondant au critère donné via l'API Google Books.
     *
     * @param critereRecherche texte de recherche (titre, auteur, mot-clé)
     * @return liste de livres trouvés correspondant au critère
     * @throws BookNotFoundException si aucun livre n'est trouvé
     * @throws ExternalApiException si une erreur survient lors de l'appel à l'API externe
     */
    public List<BookDTO> searchBooks(String critereRecherche) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + critereRecherche
                + "&langRestrict=fr&maxResults=20&key=" + apiKey + "&printType=books";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        List<BookDTO> results = new ArrayList<>();

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JSONObject root = new JSONObject(response.getBody());
                JSONArray items = root.optJSONArray("items");

                if (items == null || items.length() == 0) {
                    throw new BookNotFoundException("Aucun livre trouvé pour : " + critereRecherche);
                }

                for (int i = 0; i < items.length(); i++) {
                    JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");

                    BookDTO dto = new BookDTO();
                    dto.setTitre(volumeInfo.optString("title", "Titre inconnu"));

                    // Auteurs
                    JSONArray auteursArray = volumeInfo.optJSONArray("authors");
                    if (auteursArray != null) {
                        List<String> auteurs = new ArrayList<>();
                        for (int j = 0; j < auteursArray.length(); j++) {
                            auteurs.add(auteursArray.getString(j));
                        }
                        dto.setAuteurs(auteurs);
                    }

                    // ISBN
                    JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
                    if (industryIdentifiers != null && industryIdentifiers.length() > 0) {
                        dto.setIsbn(industryIdentifiers.getJSONObject(0).optString("identifier"));
                    }

                    // Genre
                    JSONArray categories = volumeInfo.optJSONArray("categories");
                    if (categories != null && categories.length() > 0) {
                        dto.setGenre(categories.getString(0));
                    }

                    // Couverture
                    JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
                    if (imageLinks != null) {
                        dto.setCouvertureUrl(imageLinks.optString("thumbnail"));
                    }

                    results.add(dto);
                }
                return results;

            } catch (JSONException e) {
                throw new ExternalApiException("Erreur lors du traitement de la réponse de l'API externe");
            }
        } else {
            throw new ExternalApiException("Erreur lors de l'appel à l'API externe");
        }
    }
}
