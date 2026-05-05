#include <android/native_window_jni.h>
#include <android/log.h>
#include <jni.h>
#include <mutex>
#include <vector>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "VulkanFigure", __VA_ARGS__)

struct Vertex {
    float x, y, z;
};

class Renderer {
public:
    void setSurface(ANativeWindow* window) {
        std::lock_guard<std::mutex> lock(mutex_);
        window_ = window;
        buildFigure();
        LOGI("Surface attached. Vertices in complex shape: %zu", vertices_.size());
        // Here should be full Vulkan setup: instance/device/swapchain/pipeline.
    }

    void resize(int width, int height) {
        std::lock_guard<std::mutex> lock(mutex_);
        width_ = width;
        height_ = height;
        LOGI("Resize %dx%d", width_, height_);
    }

    void destroy() {
        std::lock_guard<std::mutex> lock(mutex_);
        if (window_) {
            ANativeWindow_release(window_);
            window_ = nullptr;
        }
    }

    void rotate(float yawDelta, float pitchDelta) {
        std::lock_guard<std::mutex> lock(mutex_);
        yaw_ += yawDelta;
        pitch_ += pitchDelta;
        LOGI("Rotate figure yaw=%.2f pitch=%.2f", yaw_, pitch_);
        // In real renderer, update UBO matrix and draw next frame.
    }

private:
    void buildFigure() {
        vertices_.clear();
        // "Astrahedron": fused star + torus-like ring control points.
        for (int i = 0; i < 12; ++i) {
            float a = static_cast<float>(i) * 0.523599f;
            float r = (i % 2 == 0) ? 1.0f : 0.45f;
            vertices_.push_back({r * cosf(a), r * sinf(a), 0.25f * sinf(a * 3.0f)});
            vertices_.push_back({0.6f * cosf(a), 0.6f * sinf(a), 0.7f * cosf(a * 2.0f)});
        }
    }

    std::mutex mutex_;
    ANativeWindow* window_ = nullptr;
    int width_ = 0;
    int height_ = 0;
    float yaw_ = 0.f;
    float pitch_ = 0.f;
    std::vector<Vertex> vertices_;
};

static Renderer g_renderer;

extern "C" JNIEXPORT void JNICALL
Java_com_example_vulkanfigure_VulkanNative_onSurfaceCreated(JNIEnv* env, jobject, jobject surface) {
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    g_renderer.setSurface(window);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_vulkanfigure_VulkanNative_onSurfaceChanged(JNIEnv*, jobject, jint width, jint height) {
    g_renderer.resize(width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_vulkanfigure_VulkanNative_onSurfaceDestroyed(JNIEnv*, jobject) {
    g_renderer.destroy();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_vulkanfigure_VulkanNative_rotateFigure(JNIEnv*, jobject, jfloat yawDelta, jfloat pitchDelta) {
    g_renderer.rotate(yawDelta, pitchDelta);
}
