package com.github.tezvn.lunix;

public class PerlinNoiseExample {

    public static void main(String[] args) {
        int width = 512;
        int height = 512;
        double frequency = 0.02; // Tần số của Perlin Noise

        OpenSimplex2 noise = new OpenSimplex2(); // Sử dụng hạt giống (seed) 0

        double[][] perlinNoise = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double sampleX = x * frequency;
                double sampleY = y * frequency;
                perlinNoise[x][y] = noise.noise2(0, sampleX, sampleY);
            }
        }

        // In ra giá trị Perlin Noise
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double value = perlinNoise[x][y];
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}