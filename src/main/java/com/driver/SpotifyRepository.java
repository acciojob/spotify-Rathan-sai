package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;
    public HashMap<User, Song> LikedMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();
        LikedMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean isCreated = false;
        Artist artist = new Artist();
        for(Artist artist1 : artists){
            if(Objects.equals(artist1.getName(), artistName)){
                artist = artist1;
                isCreated = true;
                break;
            }
        }
        if(!isCreated){
            artist = createArtist(artistName);
            artists.add(artist);
        }
        Album album = new Album(title);
        albums.add(album);
        List<Album> albumsList = new ArrayList<>();
        if(artistAlbumMap.containsKey(artist)){
            albumsList = artistAlbumMap.get(artist);
        }
        albumsList.add(album);
        artistAlbumMap.put(artist, albumsList);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean hadAlbum = false;
        Album album1 = new Album();
        for(Album album : albums){
            if(Objects.equals(album.getTitle(), albumName)){
                hadAlbum = true;
                album1 = album;
                break;
            }
        }
        if(!hadAlbum){
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title, length);
        List<Song> songs1 = new ArrayList<>();
        songs.add(song);
        if(albumSongMap.containsKey(album1)){
            songs1 = albumSongMap.get(album1);
        }
        songs1.add(song);
        albumSongMap.put(album1, songs1);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(title))
                return  playlist;
        }
        Playlist playlist = new Playlist(title);
        // adding playlist to playlists list
        playlists.add(playlist);

        List<Song> temp= new ArrayList<>();
        for(Song song : songs){
            if(song.getLength()==length){
                temp.add(song);
            }
        }
        playlistSongMap.put(playlist,temp);

        User curUser= new User();
        boolean flag= false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                flag= true;
                break;
            }
        }
        if (flag==false){
            throw new Exception("User does not exist");
        }

        List<User> userslist = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            userslist=playlistListenerMap.get(playlist);
        }
        userslist.add(curUser);
        playlistListenerMap.put(playlist,userslist);

        creatorPlaylistMap.put(curUser,playlist);

        List<Playlist>userplaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(curUser)){
            userplaylists=userPlaylistMap.get(curUser);
        }
        userplaylists.add(playlist);
        userPlaylistMap.put(curUser,userplaylists);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(title))
                return  playlist;
        }
        Playlist playlist = new Playlist(title);
        // adding playlist to playlists list
        playlists.add(playlist);

        List<Song> temp= new ArrayList<>();
        for(Song song : songs){
            if(songTitles.contains(song.getTitle())){
                temp.add(song);
            }
        }
        playlistSongMap.put(playlist,temp);

        User curUser= new User();
        boolean flag= false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                flag= true;
                break;
            }
        }
        if (flag==false){
            throw new Exception("User does not exist");
        }

        List<User> userslist = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            userslist=playlistListenerMap.get(playlist);
        }
        userslist.add(curUser);
        playlistListenerMap.put(playlist,userslist);

        creatorPlaylistMap.put(curUser,playlist);

        List<Playlist>userplaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(curUser)){
            userplaylists=userPlaylistMap.get(curUser);
        }
        userplaylists.add(playlist);
        userPlaylistMap.put(curUser,userplaylists);

        return playlist;
    }


    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        boolean flag =false;
        Playlist playlist = new Playlist();
        for(Playlist curplaylist: playlists){
            if(curplaylist.getTitle().equals(playlistTitle)){
                playlist=curplaylist;
                flag=true;
                break;
            }
        }
        if (flag==false){
            throw new Exception("Playlist does not exist");
        }

        User curUser= new User();
        boolean flag2= false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                flag2= true;
                break;
            }
        }
        if (flag2==false){
            throw new Exception("User does not exist");
        }
//        public HashMap<Playlist, List<User>> playlistListenerMap;
        List<User> userslist = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            userslist=playlistListenerMap.get(playlist);
        }
        if(!userslist.contains(curUser))
            userslist.add(curUser);
        playlistListenerMap.put(playlist,userslist);
//        public HashMap<User, Playlist> creatorPlaylistMap;
        if(creatorPlaylistMap.get(curUser)!=playlist)
            creatorPlaylistMap.put(curUser,playlist);
//        public HashMap<User, List<Playlist>> userPlaylistMap;
        List<Playlist>userplaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(curUser)){
            userplaylists=userPlaylistMap.get(curUser);
        }
        if(!userplaylists.contains(playlist))userplaylists.add(playlist);
        userPlaylistMap.put(curUser,userplaylists);


        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User curUser= new User();
        boolean flag2= false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                flag2= true;
                break;
            }
        }
        if (!flag2){
            throw new Exception("User does not exist");
        }

        Song song = new Song();
        boolean flag = false;
        for(Song cursong : songs){
            if(cursong.getTitle().equals(songTitle)){
                song=cursong;
                flag=true;
                break;
            }
        }
        if (!flag){
            throw new Exception("Song does not exist");
        }

        //public HashMap<Song, List<User>> songLikeMap;
        List<User> users = new ArrayList<>();
        if(songLikeMap.containsKey(song)){
            users=songLikeMap.get(song);
        }
        if (!users.contains(curUser)){
            users.add(curUser);
            songLikeMap.put(song,users);
            song.setLikes(song.getLikes()+1);


//            public HashMap<Album, List<Song>> albumSongMap;
            Album album = new Album();
            for(Album curAlbum : albumSongMap.keySet()){
                List<Song> temp = albumSongMap.get(curAlbum);
                if(temp.contains(song)){
                    album=curAlbum;
                    break;
                }
            }


//            public HashMap<Artist, List<Album>> artistAlbumMap;
            Artist artist = new Artist();
            for(Artist curArtist : artistAlbumMap.keySet()){
                List<Album> temp = artistAlbumMap.get(curArtist);
                if(temp.contains(album)){
                    artist=curArtist;
                    break;
                }
            }

            artist.setLikes(artist.getLikes()+1);
        }
        return song;
    }
    public Album getAlbum(Song s, HashMap<Album, List<Song>> albumSongMap){
        for(Map.Entry<Album, List<Song>> albumListMap : albumSongMap.entrySet()){
            for(Song song : albumListMap.getValue()){
                if(song == s){
                    return albumListMap.getKey();
                }
            }
        }
        return new Album();
    }
    public Artist getAtrist(Album s, HashMap<Artist, List<Album>> artistAlbumMap){
        for(Map.Entry<Artist, List<Album>> albumListMap : artistAlbumMap.entrySet()){
            for(Album song : albumListMap.getValue()){
                if(song == s){
                    return albumListMap.getKey();
                }
            }
        }
        return new Artist();
    }


    public String mostPopularArtist() {
        Artist artist = new Artist();
        int max = Integer.MIN_VALUE;
        for(Artist artist1 : artists){
            if(max < artist1.getLikes()){
                artist = artist1;
                max = artist1.getLikes();
            }
        }
        return artist.getName();
    }

    public String mostPopularSong() {
        Song song = new Song();
        int max = Integer.MIN_VALUE;
        for(Song song1 : songs){
            if(max < song1.getLikes()){
                song = song1;
                max = song1.getLength();
            }
        }
        return song.getTitle();
    }
}
