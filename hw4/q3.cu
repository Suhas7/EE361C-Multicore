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

    //transmit to GPU
    printf("started gpu sect\n");
    int* cudaInp;
    int* inpLen;
    int* resArr;
    int* resLen;
    int* resLenHost;
    cudaMalloc((void**)&cudaInp,numLen*sizeof(int));
    cudaMalloc((void**)&inpLen,sizeof(int));
    cudaMalloc((void**)&resArr,numLen*sizeof(int));
    cudaMalloc((void**)&resLen,sizeof(int));
    cudaMemcpy(cudaInp, &inp, numLen*sizeof(int), cudaMemcpyHostToDevice);
    cudaMemcpy(inpLen,&numLen,sizeof(int),cudaMemcpyHostToDevice);

    //run kernel
    kern<<<NUM_BLOCKS, BLOCK_WIDTH>>>(cudaInp,inpLen,resArr, resLen);
    cudaDeviceSynchronize();
    cudaMemcpy(inp, resArr, numLen*sizeof(int), cudaMemcpyDeviceToHost);
    cudaMemcpy(resLenHost,resLen,sizeof(int),cudaMemcpyDeviceToHost);
    printf("%d\n",*resLenHost);
    for(int j=0; j<*resLenHost;j++){
        printf("%d\n",inp[j]);
    }
    cudaFree(cudaInp);
}
