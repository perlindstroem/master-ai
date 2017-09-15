ssort([X],[X]).
ssort(L,LS):-
	findMin(L,M),
	delete(M,L,T),
	ssort(T,L1),
	append([M],L1,LS).

delete(X,L,R):-
	append(H,[X|T],L),
	append(H,T,R).

findMin([X],X).
findMin(L,X):-
	append([A,B], L1, L),
	A<B,
	findMin([A|L1],X).

findMin(L,X):-
	append([A,B], L1, L),
	A>=B,
	findMin([B|L1],X).