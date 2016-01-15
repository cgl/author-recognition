I=50;J=20;
alfa = 0.5; beta=0.1;
for i=1:I,
    for j=1:J,
    	h(j) = gamrnd(alfa, 1)./beta;
	w(i) = gamrnd(alfa, 1)./beta;
	lambda = h(j)*w(i);
	X(i,j) = poissrnd(lambda);
    end
end

q=0.1:0.1:0.9;
rM = rand(I,J);
for u=1:9,
  M{u} = (rM < q(u));
end

function E = MSE(X_p,X,M),
  MSE = 0;
  count=0;
  I=50;
  J=20;
  for i=1:I,
    for j=1:J,
      if M{9}(i,j) == 0,
        e=0;
        for u=1:8,
	  MX = M{u}.*X;
          e += (MX(i,j)-X_p(i,j))**2;
        end,
	MSE += e;
	count++;
      end
    end,
  end,
  E = MSE/count;
end

#w(i) = (X(i,j)+alfa-1)/(h(j)+beta);

#(X.+alfa.-1)/(h.+beta);
function [w,h,E] = ICM(X,M)
  I=50;J=20;
  alfa = 0.5; beta=0.1;
% used gamrnd to initialize correct?
  w = gamrnd(alfa, 1,I,1)./beta;
  h = gamrnd(alfa, 1,J,1)./beta;
  XR = w*h';
  E(1) = MSE(XR,X,M);
  for i=1:200
    w = (XR.+alfa.-1)/(h'.+beta);
    XR = w*h';
    E(++i) = MSE(XR,X,M);
    h = (XR'.+alfa.-1)/(w'.+beta);
    XR = w*h';
    E(++i) = MSE(XR,X,M);
    diff = E(i-1) - E(i);
    if diff < 0.1,
      diff,E(i-1),E(i),
      break;
    end
  end
end

[w,h,E] = ICM(X,M);
plot(E);
