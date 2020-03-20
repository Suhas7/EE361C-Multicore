#include <stdio.h>
#include <string.h>

#define THREADS_PER_BLOCK 64

__global__ void part_a_cuda(int* a, int* b, int len)
{
    int a_index = threadIdx.x + blockIdx.x * THREADS_PER_BLOCK;
    int b_index = 0;
    
	if (a_index < len) 
		b_index = a[a_index] / 100;
		b[b_index]++; 
}

void part_a() {
    char buff[50000];
    int inp[10000];
    buff[0] = ' ';
    char* token;
    FILE* fp = fopen("inp.txt", "r");
    fgets(buff+1, sizeof(buff), fp);
    token = strtok(buff, ",");
    int len = 0; 

    while(token != NULL) {
		inp[len] = atoi(token+1);
		len++;
		token = strtok(NULL, ",");
    }

    int* A = (int* )malloc(sizeof(int) * len);
    int* B = (int* )malloc(sizeof(int) * 10);

    for (int i = 0; i < len; i++) {
    	A[i] = inp[i];
    }

    int *d_a, *d_b;
    cudaMalloc(&d_a, sizeof(int) * len);
    cudaMalloc(&d_b, sizeof(int) * 10);
    cudaMemcpy(d_a, A, sizeof(int) * len, cudaMemcpyHostToDevice);
    part_a_cuda<<<len/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_a, d_b, len);
    cudaDeviceSynchronize();
    cudaMemcpy(B, d_b, sizeof(int) * 10, cudaMemcpyDeviceToHost);

    for(int i = 0; i < 10; i++) {
    	printf("%d, ", B[i]);
    }

}

int main(int argc, char **argv)
{
    part_a();
    printf("That's all for part a!\n");
    //part_b()
    //printf("That's all for part b!\n");
    //part_c()
    //printf("That's all for part c!\n");

    return 0;
}