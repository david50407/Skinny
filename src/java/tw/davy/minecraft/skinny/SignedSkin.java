package tw.davy.minecraft.skinny;

/**
 * @author Davy
 */
public class SignedSkin {
    private final String mValue;
    private final String mSignature;

    public SignedSkin(final String value, final String signature) {
        mValue = value;
        mSignature = signature;
    }

    public final String getValue() {
        return mValue;
    }

    public final String getSignature() {
        return mSignature;
    }
}
