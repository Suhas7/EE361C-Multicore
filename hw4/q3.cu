#include <stdio.h>
#include <string.h>
#define NUM_BLOCKS 1
#define BLOCK_WIDTH 1

__global__ void kern(int* inp, int* inpLen, int* res, int* resLen){
    int j=0;
    for(int i = 0; i<*inpLen; i++){
        if(inp[i]%2==1){
            res[j]=inp[i];
            j++;
        }
    }
    *resLen=j;
}

__global__ void prefixSum(int* inp, int inpLen, int* res, int* resLen){
    int j=0;
    for(int i = 0; i<inpLen; i++){
        if(inp[i]%2==1){
            res[j]=inp[i];
            j++;
        }
    }
    *resLen=j;
}
__global__ void copyInt(int* dest, int* source){
    *dest=*source;
}
__global__ void copyOdds(int* inp, int* prefix, int* inpLen, int* out){
    if(prefix[0]==1) out[0]=inp[0];
    //todo parallelize this loop
    for(int i=1; i<inpLen; i++){
        if(prefix[i]!=prefix[i-1]) out[prefix[i]-1]=inp[i];
    }
}

__global__ void driver(int* cudaInp,int* inpLen,int** resArr, int* resLen){
    int* prefix;
    cudaMalloc((void**)&prefix,inpLen*sizeof(int));
    //compute prefixSum
    prefixSum(inp, *inpLen, prefix, resLen);
    //allocate output array
    cudaMalloc(*resArr, (*resLen)*sizeof(int));
    copyOdds(inp, prefix, inpLen, *resArr);
    cudaFree(prefix);
}

int main(int argc,char **argv){
    //read array in
    char buff[50000];
    int inp[5000];
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
    
    //run kernel
    kern<<<NUM_BLOCKS, BLOCK_WIDTH>>>(cudaInp,inpLen,resArr, resLen);
    cudaDeviceSynchronize();
    
    //recover data
    int resLenHost;
    cudaMemcpy(&resLenHost,resLen,sizeof(int),cudaMemcpyDeviceToHost);
    printf("Result is size %i\n",resLenHost);
    
    cudaMemcpy(inp, resArr, resLenHost*sizeof(int), cudaMemcpyDeviceToHost);
    for(int j=0; j<resLenHost;j++){
        printf("%d\n",inp[j]);
    }
    cudaFree(cudaInp);
    cudaFree(inpLen);
    cudaFree(*resArr);
    cudaFree(resLen);
}
