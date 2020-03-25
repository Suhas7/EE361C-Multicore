#include <stdio.h>
#include <string.h>

#define THREADS_PER_BLOCK 64

__global__ void part_a_cuda(int* a, int* b, int len)
{
    int a_index = threadIdx.x + blockIdx.x * THREADS_PER_BLOCK;
    int b_index = 0;
    
	if (a_index < len) {
		b_index = a[a_index] / 100;
		atomicAdd(&b[b_index], 1);
	}
}

__global__ void part_b_cuda(int* a, int* b, int len)
{
	int a_index = threadIdx.x + blockIdx.x * THREADS_PER_BLOCK;
    int b_index = 0;
    __shared__ int temp[10];
    
	if (a_index < len) {
		b_index = a[a_index] / 100;
		atomicAdd(&temp[b_index], 1);
	}

	__syncthreads();

	if (threadIdx.x == 0) {
		for (int i = 0; i < 10; i++) {
			atomicAdd(&b[i], temp[i]);
		}
	}
}

__global__ void part_c_cuda(int* a, int* b)
{
	//part c, prefix sum with 10 elements
}

void part_a() {
	//gather input from files
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

    //cuda stuff
    int *d_a, *d_b;
    cudaMalloc(&d_a, sizeof(int) * len);
    cudaMalloc(&d_b, sizeof(int) * 10);
    cudaMemcpy(d_a, A, sizeof(int) * len, cudaMemcpyHostToDevice);
    part_a_cuda<<<(len + THREADS_PER_BLOCK)/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_a, d_b, len);
    cudaDeviceSynchronize();
    cudaMemcpy(B, d_b, sizeof(int) * 10, cudaMemcpyDeviceToHost);

    //Print final values
    /*
    for(int i = 0; i < 10; i++) {
    	printf("%d, ", B[i]);
    }
    */
    fclose(fp);
	FILE* fp_end = fopen("q2a.txt", "w");
	for (int i = 0; i < 10; i++) {
		fputc(B[i] + '0', fp_end);
		if (i != 9) {
			fputc(', ', fp_end);
		}
	}
	fclose(fp_end);
	cudaFree(d_a);
	cudaFree(d_b);
}

void part_b() {
	//gather input from files
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

    //cuda stuff
    int *d_a, *d_b;
    cudaMalloc(&d_a, sizeof(int) * len);
    cudaMalloc(&d_b, sizeof(int) * 10);
    cudaMemcpy(d_a, A, sizeof(int) * len, cudaMemcpyHostToDevice);
    part_b_cuda<<<(len + THREADS_PER_BLOCK)/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_a, d_b, len);
    cudaDeviceSynchronize();
    cudaMemcpy(B, d_b, sizeof(int) * 10, cudaMemcpyDeviceToHost);

    //Print final values
    /*
    for(int i = 0; i < 10; i++) {
    	printf("%d, ", B[i]);
    }
    */
    fclose(fp);
	FILE* fp_end = fopen("q2b.txt", "w");
	for (int i = 0; i < 10; i++) {
		fputc(B[i] + '0', fp_end);
		if (i != 9) {
			fputc(', ', fp_end);
		}
	}
	fclose(fp_end);
	cudaFree(d_a);
	cudaFree(d_b);
}

void part_c() {
	//gather input from files
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
    int* C = (int* )malloc(sizeof(int) * 10);

    for (int i = 0; i < len; i++) {
    	A[i] = inp[i];
    }

    //cuda stuff
    int *d_a, *d_b, *d_c;
    cudaMalloc(&d_a, sizeof(int) * len);
    cudaMalloc(&d_b, sizeof(int) * 10);
    cudaMemcpy(d_a, A, sizeof(int) * len, cudaMemcpyHostToDevice);
    part_b_cuda<<<(len + THREADS_PER_BLOCK)/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_a, d_b, len);
    cudaDeviceSynchronize();

    fclose(fp);
    cudaFree(d_a);

	//now do prefix sum of 10 elements in b
    cudaMalloc(&d_c, sizeof(int) * 10);
    part_c_cuda<<<(10 + THREADS_PER_BLOCK)/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_b, d_c);
    cudaDeviceSynchronize();
    cudaMemcpy(d_c, C, sizeof(int) * 10, cudaMemcpyDeviceToHost);

    //copy stuff to file
	FILE* fp_end = fopen("q2c.txt", "w");
	for (int i = 0; i < 10; i++) {
		fputc(C[i] + '0', fp_end);
		if (i != 9) {
			fputc(', ', fp_end);
		}
	}
	fclose(fp_end);
	
}

int main(int argc, char **argv)
{
    part_a();
    part_b();
    part_c();
    return 0;
}