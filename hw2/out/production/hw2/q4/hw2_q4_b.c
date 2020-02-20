#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <math.h>

int countPrimes(int arr[], int len) {
    int i, count = 0;
    for(i = 2; i < len; i++) {
	if(!arr[i])
	    count++;
    }
    return count; 
}

int Sieve(int N, int threads){
    //Write your code here
    int i, j;
    int stop = (int)sqrt(N);
    int *arr = malloc(sizeof(int) * (N+1));
    omp_set_num_threads(N);
#pragma omp for  
    for(i = 2; i < stop; i++) {
   	if(!arr[i]) {
	    for(j = i+i; j < N+1; j+=i) {
		arr[j] = 1;
	    } 
	}
    }
    return countPrimes(arr, N+1);
}

void main2(void) {

    int num_primes;
    int num_threads = 1;

    num_primes = Sieve(100000000, num_threads);
    printf("%d", num_primes);
}
