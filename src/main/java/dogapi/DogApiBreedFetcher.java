package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        try {
            // construct the API URL for fetching sub-breeds
            String url = "https://dog.ceo/api/breed/" + breed + "/list";
            
            // create HTTP request
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            
            // execute request
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new BreedNotFoundException(breed);
                }
                
                // parse JSON response
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                
                // check if the API call was successful
                String status = jsonResponse.getString("status");
                if (!"success".equals(status)) {
                    throw new BreedNotFoundException(breed);
                }
                
                // extract sub-breeds from the message array
                JSONArray subBreedsArray = jsonResponse.getJSONArray("message");
                List<String> subBreeds = new ArrayList<>();
                
                for (int i = 0; i < subBreedsArray.length(); i++) {
                    subBreeds.add(subBreedsArray.getString(i));
                }
                
                return subBreeds;
            }
        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        } catch (Exception e) {
            throw new BreedNotFoundException(breed);
        }
    }
}