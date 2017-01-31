t<- build_gradle[,4]
t2 <- build_gradle[, 5]
t3 <- build_gradle[, 6]

cova <- cov(cbind(t, t2, t3))

corre <- cor(cbind(t,t2, t3))

ccfaaa <- ccf(t, t3, 5)

