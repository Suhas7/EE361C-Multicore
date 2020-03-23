#include <stdio.h>
#include <string.h>
#define NUM_BLOCKS 1
#define BLOCK_WIDTH 1

//todo this needs parallelization
__device__ void prefixSum(int* inp, int* inpLen, int* res, int* resLen){
    int runningTotal=0;
    int length=*inpLen;
    for(int i=0; i<length;i++){
        if((inp[i]%2)==1) runningTotal++;
        res[i]=runningTotal;
    }
    *resLen=runningTotal;
}

__device__ void copyInt(int* dest, int* source){
    *dest=*source;
}

__device__ void copyOdds(int* inp, int* prefix, int* inpLen, int* out){
    if(prefix[0]==1) out[0]=inp[0];
    //todo parallelize this loop
    for(int i=1; i<*inpLen; i++){
        if(prefix[i]!=prefix[i-1]) inp[prefix[i]-1]=inp[i];
    }
}

__global__ void driver(int* cudaInp,int* inpLen,int** resArr, int* resLen){
    int* prefix = (int*) malloc((*inpLen)*sizeof(int));
    //compute prefixSum
    prefixSum(cudaInp, inpLen, prefix,resLen);
    //allocate output array
    *resArr = (int*) malloc((*resLen)*sizeof(int));
    //postprocess to make an array of odds
    *resLen=prefix[*inpLen-1];
    copyOdds(cudaInp, prefix, inpLen, *resArr);
    //print output
    for(int i=0; i<*resLen; i++){
        printf("%d\n",cudaInp[i]);
    }
    free(prefix);
}

int main(int argc,char **argv){
    //read array in
    char buff[50000];
    int inp[15000];
    buff[0]=' ';
    char* token;
    FILE* fp = fopen("inp.txt", "r" );
    fgets(buff+1, 50000, fp);
    token=strtok(buff,",");
    int numLen=0;
    while(token!=NULL){
        inp[numLen]=atoi(token+1);
        numLen++;
        token=strtok(NULL,",");
    }
    //GPU data transfer
    printf("started gpu sect\n");
    int* cudaInp;
    cudaMalloc((void**)&cudaInp,numLen*sizeof(int));
    cudaMemcpy(cudaInp, inp, numLen*sizeof(int), cudaMemcpyHostToDevice);
    int* inpLen;
    cudaMalloc((void**)&inpLen,sizeof(int));
    cudaMemcpy(inpLen,&numLen,sizeof(int),cudaMemcpyHostToDevice);
    int* resLen;
    cudaMalloc((void**)&resLen,sizeof(int));
    int** resArr;
    cudaMalloc((void**)&resArr,sizeof(int*));
    
    //run kernel
    driver<<<NUM_BLOCKS, BLOCK_WIDTH>>>(cudaInp,inpLen,resArr, resLen);
    cudaDeviceSynchronize();

    //recover data
    int resLenHost=7;
    cudaMemcpy(&resLenHost,resLen,sizeof(int),cudaMemcpyDeviceToHost);
    printf("Result is size %i\n",resLenHost);
    /*
    cudaFree(cudaInp);
    cudaFree(inpLen);
    cudaFree(*resArr);
    cudaFree(resLen);
*/}
