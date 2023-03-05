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
        if(albumSongMap.containsKey(album1)){
            songs1 = albumSongMap.get(album1);
        }
        songs1.add(song);
        albumSongMap.put(album1, songs1);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        boolean hasUser = false;
        User user = new User();
        for(User user1 : users){
            if(Objects.equals(user1.getMobile(), mobile)){
                user = user1;
                hasUser = true;
                break;
            }
        }
        if(!hasUser)
            throw new Exception("User does not exist");
        List<Playlist> playlist = new ArrayList<>();
        Playlist playlist1 = new Playlist(title);
        if(userPlaylistMap.containsKey(user)){
            playlist = userPlaylistMap.get(user);
        }
        playlist.add(playlist1);
        List<Song> songs1 = new ArrayList<>();
//        if(playlistSongMap.containsKey(playlist1)){
//            songs1 = playlistSongMap.get(playlist1);
//        }
        for(Song song : songs){
            if(song.getLength() > length){
                songs1.add(song);
            }
        }
        creatorPlaylistMap.put(user, playlist1);
        playlistSongMap.put(playlist1, songs1);
        return playlist1;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        boolean hasUser = false;
        User user = new User();
        for(User user1 : users){
            if(Objects.equals(user1.getMobile(), mobile)){
                user = user1;
                hasUser = true;
                break;
            }
        }
        if(!hasUser)
            throw new Exception("User does not exist");
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> songs1 = new ArrayList<>();
//        if(playlistSongMap.containsKey(playlist)){
//            songs1 = playlistSongMap.get(playlist);
//        }
        for(String s : songTitles){
            for(Song song : songs){
                if(Objects.equals(s, song.getTitle())){
                    songs1.add(song);
                }
            }
        }
        creatorPlaylistMap.put(user, playlist);
        playlistSongMap.put(playlist, songs1);
        return playlist;
    }


    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        boolean hadUser = false;
        User user = new User();
        for(User user1 : users){
            if(Objects.equals(user1.getMobile(), mobile)){
                hadUser = true;
                user = user1;
                break;
            }
        }
        if(!hadUser){
            throw new Exception("User does not exist");
        }
        boolean hasplayList = false;
        Playlist playlist = new Playlist();
        for(Playlist playlist1 : playlists){
            if(Objects.equals(playlist1.getTitle(), playlistTitle)){
                playlist =playlist1;
                hasplayList = true;
            }
        }
        if(!hasplayList){
            throw new Exception("Playlist does not exist");
        }
        User user1 = new User();
        for(Map.Entry<User, Playlist> userPlaylistEntry : creatorPlaylistMap.entrySet()){
            if(Objects.equals(userPlaylistEntry.getValue().getTitle(), playlistTitle)){
                user1 = userPlaylistEntry.getKey();
            }
        }
        creatorPlaylistMap.remove(user1);
        creatorPlaylistMap.put(user, playlist);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        boolean hadUser = false;
        User user = new User();
        for(User user1 : users){
            if(Objects.equals(user1.getMobile(), mobile)){
                hadUser = true;
                user = user1;
                break;
            }
        }
        if(!hadUser){
            throw new Exception("User does not exist");
        }
        boolean hasSong = false;
        Song song = new Song();
        for(Song s : songs){
            if(Objects.equals(s.getTitle(), songTitle)){
                song = s;
                hasSong = true;
                break;
            }
        }
        if(!hasSong){
            throw new Exception("Song does not exist");
        }
        Album album = getAlbum(song, albumSongMap);
        Artist artist = getAtrist(album, artistAlbumMap);
        if(!LikedMap.containsKey(user)){
            LikedMap.put(user, song);
            int i = song.getLikes();
            i++;
            song.setLikes(i);
            artist.setLikes(artist.getLikes() + 1);
        }
        else{
            artist.setLikes(artist.getLikes() + 1);
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
