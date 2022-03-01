class NewFeatureZos implements NewFeature {

    static {
        NativeLibraryUtils.loadLibrary("feature");
    }

    @Override
    public native void doStuff();

}