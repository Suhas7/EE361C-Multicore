cmake_minimum_required(VERSION 3.15)
project(q4 C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fopenmp")
include_directories(.)

add_executable(q4
        hw2_q4_a.c
        )# hw2_q4_b.c)
