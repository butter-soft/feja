import { registerWebModule, NativeModule } from "expo";

// FejaModule is not available on the web platform.
class FejaModule extends NativeModule<{}> {}

export default registerWebModule(FejaModule, "FejaModule");
