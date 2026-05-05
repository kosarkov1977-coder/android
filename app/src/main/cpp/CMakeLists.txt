cmake_minimum_required(VERSION 3.22.1)
project(vulkan_figure)

add_library(vulkan_figure SHARED vulkan_figure.cpp)

find_library(log-lib log)
find_library(android-lib android)
find_library(vulkan-lib vulkan)

target_link_libraries(vulkan_figure ${log-lib} ${android-lib} ${vulkan-lib})
