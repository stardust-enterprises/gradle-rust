use jni::JNIEnv;

#[no_mangle]
pub extern "system" fn Java_ai_arcblroth_cargo_example_Main_doStuff(_env: JNIEnv) {
    println!("Hello Rust!");
}
