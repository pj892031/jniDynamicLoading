import lombok.experimental.UtilityClass;

@UtilityClass
class Factory {

    public NewFeature getNewFeature() {
        if ("z/OS".equals(System.getProperty("os.name"))) {
            return new NewFeatureZos();
        } else {
            return new NewFeatureDummy();
        }
    }

}