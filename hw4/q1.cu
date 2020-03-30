#include <stdio.h>
#include <string.h>

#define THREADS_PER_BLOCK 32

__global__ void minA_cuda(int* a, int* b, int len, int n_output) {
	int b_index = threadIdx.x + blockIdx.x * THREADS_PER_BLOCK;
    int a_index = b_index * 2;
    
    if (b_index < n_output) {
	    if (a_index == len) {
	    	b[b_index] = a[a_index];
	    }
	    else {
		    int v1 = a[a_index];
		    int v2 = a[a_index + 1];
		    if (v2 < v1) {
		    	b[b_index] = v2;
		    }
		    else {
		    	b[b_index] = v1;
		    }
		}
	}
}

__global__ void last_digit_cuda(int* a, int* b, int len) {
	int index = threadIdx.x + blockIdx.x * THREADS_PER_BLOCK;
	if (index < len) 
		b[index] = a[index] % 10;
}

void last_digit() {
	//Get input data from files
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

    //Cuda stuff
    int *d_a, *d_b;
    cudaMalloc(&d_a, sizeof(int) * len);
    cudaMalloc(&d_b, sizeof(int) * len);
    cudaMemcpy(d_a, A, sizeof(int) * len, cudaMemcpyHostToDevice);

    last_digit_cuda<<<(len + THREADS_PER_BLOCK)/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_a, d_b, len);
    cudaDeviceSynchronize();
    cudaMemcpy(B, d_b, sizeof(int) * len, cudaMemcpyDeviceToHost);

    //put results in file
	fclose(fp);
	FILE* fp_end = fopen("q1b.txt", "w");
	for (int i = 0; i < len; i++) {
		fprintf(fp_end, "%d", B[i]);
		if (i != len-1) {
			fprintf(fp_end, "%s", ", ");
		}
	}

	//Free up memory
	cudaFree(d_a);
	cudaFree(d_b);
	free(A);
	free(B);
	fclose(fp_end);
}

void minA() {
	//Get input from files
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

    //Copy input to array of proper size
    int* A = (int* )malloc(sizeof(int) * len);
    for (int i = 0; i < len; i++) {
    	A[i] = inp[i];
    }

    //Cuda stuff
    int B_size = (len + 1) / 2;
    int* B;

    while (len != 1) {
    	B = (int* )malloc(sizeof(int) * B_size);

		int *d_a, *d_b;
	    cudaMalloc(&d_a, sizeof(int) * len);
	    cudaMalloc(&d_b, sizeof(int) * B_size);
	    cudaMemcpy(d_a, A, sizeof(int) * len, cudaMemcpyHostToDevice);

	    minA_cuda<<<(B_size + THREADS_PER_BLOCK)/THREADS_PER_BLOCK, THREADS_PER_BLOCK>>>(d_a, d_b, len, B_size);
	    cudaDeviceSynchronize();
	    cudaMemcpy(B, d_b, sizeof(int) * B_size, cudaMemcpyDeviceToHost);

	    cudaFree(d_a);
	    cudaFree(d_b);
	    memcpy(A, B, B_size * sizeof(int));
	    free(B);
	    len = B_size;
	    B_size = (len + 1) / 2;
    }

    //Print output to file
    fclose(fp);
	FILE* fp_end = fopen("q1a.txt", "w");
	fprintf(fp_end, "%d", A[0]);
	fclose(fp_end);
}

int main(int argc,char **argv)
{
    minA();
    cudaDeviceReset();
    last_digit();
    return 0;
}
