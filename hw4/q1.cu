#include <stdio.h>
#include <string.h>

#define THREADS_PER_BLOCK 32

__global__ void minA_cuda(int* a, int* b, int len) {
    //Min A
}

__global__ void last_digit_cuda(int* a, int* b, int len) {
	int index = threadIdx.x + blockIdx.x * THREADS_PER_BLOCK;
	if (index < len) 
		b[index] = a[index] % 10;
}

void last_digit() {
    char buff[50000];
    int inp[10000];
    buff[0] = ' ';
    char* token;
    FILE* fp = fopen("inp.txt", "r");
    fgets(buff+1,sizeof(buff), fp);
    token = strtok(buff, ",");
    int len = 0; 
    while(token != NULL) {
		inp[len] = atoi(token+1);
		len++;
		token = strtok(NULL, ",");
    }

    int* A = (int* )malloc(sizeof(int) * len);
    int* B = (int* )malloc(sizeof(int) * len);

    for (int i = 0; i < len; i++) {
    	A[i] = inp[i];
    }

    int *d_a, *d_b;
    cudaMalloc(&d_a, sizeof(int) * len);
    cudaMalloc(&d_b, sizeof(int) * len);
    cudaMemcpy(d_a, A, sizeof(int) * len, cudaMemcpyHostToDevice);

    last_digit_cuda<<<len/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_a, d_b, len);
    cudaDeviceSynchronize();
    cudaMemcpy(B, d_b, sizeof(int) * len, cudaMemcpyDeviceToHost);

    for(int i =0; i < len; i++) {
    	printf("%d, ", B[i]);
    }

	printf("That's all!\n");
}

void minA() {
    char buff[50000];
    int inp[10000];
    buff[0] = ' ';
    char* token;
    FILE* fp = fopen("inp.txt", "r");
    fgets(buff+1,sizeof(buff), fp);
    token = strtok(buff, ",");
    int len = 0; 
    while(token != NULL) {
		inp[len] = atoi(token+1);
		len++;
		token = strtok(NULL, ",");
    }

    int* A = (int* )malloc(sizeof(int) * len);
    for (int i = 0; i < len; i++) {
    	A[i] = inp[i];
    }

}

int main(int argc,char **argv)
{
    // launch the kernel
    //hello<<<NUM_BLOCKS, BLOCK_WIDTH>>>();

    // force the printf()s to flush
    //cudaDeviceSynchronize();

    //printf("That's all!\n");
    last_digit();

    return 0;
}
