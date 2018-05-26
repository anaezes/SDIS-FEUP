package xet.server;

public class User {
    private String id;
    private String providerId;
    private String name;
    private String pictureUrl;

    public User(String id, String providerId, String name) {
        this.id = id;
        this.name = name;
        this.providerId = providerId;
        this.pictureUrl = "";
    }

    public User(String id, String providerId, String name, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.providerId = providerId;
        this.pictureUrl = pictureUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }
}
