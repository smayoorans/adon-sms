CREATE TABLE IF NOT EXISTS `oauth2` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `accessToken` varchar(255) NOT NULL,
  `refreshToken` varchar(255) NOT NULL,
  `consumerKey` varchar(255) NOT NULL,
  `consumerSecret` varchar(255) NOT NULL,
  `scope` varchar(20) NOT NULL,
  `expire` int(10) NOT NULL,
  `tokenURL` text NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `subscribers` (
  `msisdn` varchar(255) NOT NULL,
  `ref` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  PRIMARY KEY (`msisdn`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO oauth2(accessToken, refreshToken, consumerKey, consumerSecret, scope, expire, tokenURL, lastUpdated)
VALUES ('7bacb9508ac8f8b06457776e993c81', 'e6bfbda933fa68074ca293686ce5d27', 'LupDbfxIGbMXGRdyXukORD1GP48a',
        'ePp0sojUUrEhViGITp_GoPTxMh0a','PRODUCTION', 3000 ,'https://ideabiz.lk/apicall/token', now() )

