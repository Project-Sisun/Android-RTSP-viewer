#include "sisun_kokkiri_sisun_JNIAdapter_FFmpegAdapter.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "FFmpeg_DEBUGING", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "FFmpeg_DEBUGING", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "FFmpeg_DEBUGING", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "FFmpeg_DEBUGING", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "FFmpeg_DEBUGING", __VA_ARGS__)

char *uri;

JNIEXPORT void JNICALL Java_sisun_kokkiri_sisun_JNIAdapter_FFmpegAdapter_setDataSource(JNIEnv *env, jclass clazz, jstring _uri){
    uri = (*env)->GetStringUTFChars(env, _uri, NULL);
}


JNIEXPORT jint JNICALL Java_sisun_kokkiri_sisun_JNIAdapter_FFmpegAdapter_play(JNIEnv * env, jclass clazz, jobject surface)
{
    LOGD("player init, source url>> %s\n", uri);
   const char * file_name = uri ;
    if( file_name == NULL ) {
        LOGE("Please set the DataSource");
        return -1;
    }

/*------------ 라이브러리 초기화 단계 ------------*/

    av_register_all();

    avformat_network_init();

    AVFormatContext * pFormatCtx = avformat_alloc_context();

    if(avformat_open_input(&pFormatCtx, file_name, NULL, NULL)!=0) {
        LOGE("Couldn't open file:%s\n", file_name);
        return -1; // Couldn't open file
    }

/*------------ 미디어 파일 정보 가져오기 단계 ------------*/

    if(avformat_find_stream_info(pFormatCtx, NULL)<0) {
        LOGE("Couldn't find stream information.");
        return -1;
    }

    int videoStream = -1, i;
    for (i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO
           && videoStream < 0) {
            videoStream = i;
        }
    }
    if(videoStream==-1) {
        LOGE("Didn't find a video stream.");
        return -1; // Didn't find a video stream
    }

/*------------ 디코더 준비 단계 ------------*/

    AVCodecContext  * pCodecCtx = pFormatCtx->streams[videoStream]->codec;

    AVCodec * pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    if(pCodec==NULL) {
        LOGE("Codec not found.");
        return -1; // Codec not found
    }

    if(avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
        LOGE("Could not open codec.");
        return -1; // Could not open codec
    }

    int videoWidth = pCodecCtx->width;
    int videoHeight = pCodecCtx->height;

    LOGD("videoWidth >> %d, videoHeight >> %d\n", videoWidth, videoHeight);

    // Allocate video frame
    AVFrame * pFrame = av_frame_alloc();

    // rendering
    AVFrame * pFrameRGBA = av_frame_alloc();

    if(pFrameRGBA == NULL || pFrame == NULL) {
        LOGE("Could not allocate video frame.");
        return -1;
    }

    int numBytes = av_image_get_buffer_size(AV_PIX_FMT_RGBA, pCodecCtx->width, pCodecCtx->height, 1);

    uint8_t * buffer=(uint8_t *)av_malloc(numBytes*sizeof(uint8_t));

    av_image_fill_arrays(pFrameRGBA->data, pFrameRGBA->linesize, buffer, AV_PIX_FMT_RGBA,
                         pCodecCtx->width, pCodecCtx->height, 1);

    struct SwsContext *sws_ctx = sws_getContext(pCodecCtx->width,
                             pCodecCtx->height,
                             pCodecCtx->pix_fmt,
                             pCodecCtx->width,
                             pCodecCtx->height,
                             AV_PIX_FMT_RGBA,
                             SWS_BILINEAR,
                             NULL,
                             NULL,
                             NULL);

    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env, surface);

    ANativeWindow_setBuffersGeometry(nativeWindow,  videoWidth, videoHeight, WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer windowBuffer;

/*------------ 디코딩 단계 ------------*/


    int frameFinished;

    AVPacket packet;

    LOGD("Decoding Start!!!!\n");
    while(av_read_frame(pFormatCtx, &packet)>=0) {

        if(packet.stream_index==videoStream) {

            // Decode video frame
            avcodec_decode_video2(pCodecCtx, pFrame, &frameFinished, &packet);

            if (frameFinished) {

                // lock native window buffer
                if (ANativeWindow_lock(nativeWindow, &windowBuffer, 0) < 0) {
                    LOGE("native window lock fail\n");
                    return -1;
                }

                // convert format
                sws_scale(sws_ctx, (uint8_t const * const *)pFrame->data,
                          pFrame->linesize, 0, pCodecCtx->height,
                          pFrameRGBA->data, pFrameRGBA->linesize);

                // calculate stride.
                uint8_t * dst = windowBuffer.bits;
                int dstStride = windowBuffer.stride * 4;
                uint8_t * src = (uint8_t*) (pFrameRGBA->data[0]);
                int srcStride = pFrameRGBA->linesize[0];

                // Depending on the stride and gait window frame, thus requiring a progressive copy
                int h;
                for (h = 0; h < videoHeight; h++) {
                    memcpy(dst + h * dstStride, src + h * srcStride, srcStride);
                }


                if (ANativeWindow_unlockAndPost(nativeWindow) < 0) {
                    LOGE("native window unlock fail\n");
                    return -1;
                }
            }

        }
        av_packet_unref(&packet);
    }

    avformat_network_deinit();
    av_free(buffer);
    av_free(pFrameRGBA);

    // Free the YUV frame
    av_free(pFrame);

    // Close the codecs
    avcodec_close(pCodecCtx);

    // Close the video file
    avformat_close_input(&pFormatCtx);
    return 0;

}