package com.pagepals.circle.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pagepals.circle.dto.BookDTO;
import com.pagepals.circle.exception.BookNotFoundException;
import com.pagepals.circle.exception.ExternalApiException;

@Service
public class BookSearchService {

    @Value("${google.books.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public BookDTO searchBook(String critereRecherche) {
    String url = "https://www.googleapis.com/books/v1/volumes?q=" + critereRecherche + "&langRestrict=fr&maxResults=1&key=" + apiKey;

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
        try {
            JSONObject root = new JSONObject(response.getBody());
            JSONArray items = root.getJSONArray("items");

            if (items.isEmpty()) {
                throw new BookNotFoundException("Aucun livre trouvé pour : " + critereRecherche);
            }

            JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");

            BookDTO dto = new BookDTO();
            dto.setTitre(volumeInfo.optString("title", "Titre inconnu"));

            // Auteurs
            JSONArray auteursArray = volumeInfo.optJSONArray("authors");
            if (auteursArray != null) {
                List<String> auteurs = new ArrayList<>();
                for (int i = 0; i < auteursArray.length(); i++) {
                    auteurs.add(auteursArray.getString(i));
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

            return dto;

        } catch (JSONException e) {
            throw new ExternalApiException("Erreur lors de l'appel à l'API externe");
        }
    } else {
        throw new ExternalApiException("Erreur lors de l'appel à l'API externe");

    }
}

}
